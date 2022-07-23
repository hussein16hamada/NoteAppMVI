package com.hussein.note.Note.ViewModels

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hussein.note.Note.models.Responses.NoteResponseItem
import com.hussein.note.auth.model.RegisterLoginResponse
import com.hussein.note.Note.models.NoteModel
import com.hussein.note.base.MyApplication
import com.hussein.note.logIn.api.NoteAPIManger
import com.hussein.aya.Activities.QuranListen.Intent.NoteIntent
import com.hussein.aya.Activities.QuranListen.State.NoteViewState
import io.reactivex.CompletableObserver
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class NoteViewModel : ViewModel() {
    val loading: MutableLiveData<Boolean?> = MutableLiveData(null)
    val userLoggInRegisterResponse: MutableLiveData<RegisterLoginResponse> = MutableLiveData()

    val adLoading: SingleLiveEvent<Boolean?> = SingleLiveEvent()

    val addNoteMutable: SingleLiveEvent<NoteResponseItem> = SingleLiveEvent()
    var tinyDB = com.hussein.note.base.TinyDB(MyApplication.appContext)
    private val disposable: CompositeDisposable = CompositeDisposable()
    private val TAG: String = "nnnnnnnnnnnn"

    val intentChannel = Channel<NoteIntent>(Channel.UNLIMITED)
    val viewState = MutableStateFlow<NoteViewState>(NoteViewState.ideal)

    val dbIntentChannel = Channel<NoteIntent>(Channel.UNLIMITED)
    val dbViewState = MutableStateFlow<NoteViewState>(NoteViewState.ideal)

    val addNoteIntentChannel = Channel<NoteIntent>(Channel.UNLIMITED)
    val addNoteViewState = MutableStateFlow<NoteViewState>(NoteViewState.ideal)

    val offlineNoteIntentChannel = Channel<NoteIntent>(Channel.UNLIMITED)
    val offlineNoteViewState = MutableStateFlow<NoteViewState>(NoteViewState.ideal)

    init {
        process()
    }

    private fun process() {
        viewModelScope.launch (Dispatchers.IO){
            offlineNoteIntentChannel.consumeAsFlow().collect {
                when (it) {
                    is NoteIntent.offlineNotesDB -> offlineNotesFromDb()
//                   is NoteIntent.getNotesFromDB -> getNotesFromDataBase()

                }
            }
        }
        viewModelScope.launch (Dispatchers.IO){
            intentChannel.consumeAsFlow().collect {
                when (it) {
                    is NoteIntent.getNotesFromApi -> getNotesFromApi()
//                   is NoteIntent.getNotesFromDB -> getNotesFromDataBase()

                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            dbIntentChannel.consumeAsFlow().collect {
                when (it) {
//                    is NoteIntent.getNotesFromApi -> getNotesFromApi()
                    is NoteIntent.getNotesFromDB -> getNotesFromDataBase()

                }
            }
        }

        viewModelScope.launch {
            addNoteIntentChannel.consumeAsFlow().collect {
                when (it) {
//                    is NoteIntent.getNotesFromApi -> getNotesFromApi()
                    is NoteIntent.addNoteToApi -> uploadNoteToApi(it.noteModel)

                }
            }
        }
    }

    private fun offlineNotesFromDb() {
        viewModelScope.launch (Dispatchers.IO){
            offlineNoteViewState.value = NoteViewState.laoding
            offlineNoteViewState.value = try {
                val res = com.hussein.note.Note.offlineDb.NoteDataBase.getInstance(MyApplication.appContext).noteDao().getOfflineNotesMvi(false)
                NoteViewState.NoteListSuccess(res)

            } catch (e: Exception) {
                NoteViewState.error(e.localizedMessage)
            }
        }
    }

    private fun uploadNoteToApi(noteModel: NoteModel) {
        viewModelScope.launch {
            addNoteViewState.value = NoteViewState.laoding
            addNoteViewState.value = try {
                val res = NoteAPIManger.getApis()?.addNoteMvi(tinyDB.getString("token"),noteModel)
                NoteViewState.addNoteSuccess(res?.body()!!)

            } catch (e: Exception) {
                NoteViewState.error(e.localizedMessage)
            }
        }
    }

    private fun getNotesFromDataBase() {
        viewModelScope.launch {
            dbViewState.value = NoteViewState.laoding
            dbViewState.value = try {
                val res = com.hussein.note.Note.offlineDb.NoteDataBase.getInstance(MyApplication.appContext).noteDao().allNotesMvi()
                NoteViewState.NoteListSuccess(res)

            } catch (e: Exception) {
                NoteViewState.error(e.localizedMessage)
            }
        }
    }

    private fun getNotesFromApi() {
        viewModelScope.launch {
            viewState.value = NoteViewState.laoding
            viewState.value = try {
                val res = NoteAPIManger.getApis()?.getUserNotesMvi(tinyDB.getString("token"))
                NoteViewState.NoteListSuccess(res?.body()!!)

            } catch (e: Exception) {
                NoteViewState.error(e.localizedMessage)
            }
        }
    }


    fun addNoteTODataBase(noteResponseItem: NoteResponseItem) {
        adLoading.postValue(true)
        com.hussein.note.Note.offlineDb.NoteDataBase.getInstance(MyApplication.appContext)
            .noteDao()
            .addNote(noteResponseItem)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onComplete() {
                    Toast.makeText(
                        MyApplication.appContext,
                        "done add note to db",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    Log.d(TAG, "onComplete addNoteTODataBase: ")

                    adLoading.postValue(false)
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(MyApplication.appContext, e.message, Toast.LENGTH_SHORT)
                        .show()
                    Log.d(TAG, "onComplete onError: ${e.localizedMessage}")

                }
            })
    }

    fun addListOfNotesTODataBase(listOfNotes: List<NoteResponseItem>) {
        com.hussein.note.Note.offlineDb.NoteDataBase.getInstance(MyApplication.appContext)
            .noteDao()
            .addNotes(listOfNotes)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onComplete() {
//                    Toast.makeText(
//                        MyApplication.appContext,
//                        " add all notes to db",
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
                    Log.d(TAG, "onComplete addListOfNotesTODataBase: ")

                }

                override fun onError(e: Throwable) {
                    Toast.makeText(MyApplication.appContext, e.message, Toast.LENGTH_SHORT)
                        .show()
                    Log.d(TAG, "onComplete onError: ${e.localizedMessage}")

                }
            })
    }


    fun deleteAllNotes() {
        com.hussein.note.Note.offlineDb.NoteDataBase.getInstance(MyApplication.appContext).noteDao().deleteAllNotes()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onComplete() {
                    Toast.makeText(
                        MyApplication.appContext,
                        "done deleting",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(
                        MyApplication.appContext,
                        "error in deleting",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    fun deleteFromDataBaseWithId(id: Int) {
        loading.postValue(true)

        val single = com.hussein.note.Note.offlineDb.NoteDataBase.getInstance(MyApplication.appContext)
            .noteDao()
            .deleteNote(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        val observer: CompletableObserver = object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {

            }

            override fun onComplete() {
                loading.postValue(false)
                Log.d(TAG, "onComplete: done deleting from room db")
//                Toast.makeText(
//                    MyApplication.appContext,
//                    "done deleting",
//                    Toast.LENGTH_SHORT
//                ).show()
            }


            override fun onError(e: Throwable) {
                loading.postValue(false)

                Toast.makeText(
                    MyApplication.appContext,
                    "error in deleting",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        single.subscribe(observer)
    }

    fun deleteFromDataBaseWithTitle(title: String) {

        val single = com.hussein.note.Note.offlineDb.NoteDataBase.getInstance(MyApplication.appContext)
            .noteDao()
            .deleteNoteWithTitle(title)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        val observer: CompletableObserver = object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {

            }

            override fun onComplete() {
                loading.postValue(false)
                Toast.makeText(
                    MyApplication.appContext,
                    "done deleting with title",
                    Toast.LENGTH_SHORT
                ).show()
            }


            override fun onError(e: Throwable) {

                Toast.makeText(
                    MyApplication.appContext,
                    "error in deleting",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        single.subscribe(observer)
    }



    fun addNote(noteModel: NoteModel) {
        loading.postValue(true)
        val single: Single<NoteResponseItem> =
            NoteAPIManger.getApis()?.addNote(tinyDB.getString("token"), noteModel)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread()) as Single<NoteResponseItem>

        val singleObserver: SingleObserver<NoteResponseItem> =
            object : SingleObserver<NoteResponseItem> {
                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onSuccess(t: NoteResponseItem) {
                    addNoteMutable.postValue(t)
//                    addNoteMutableee.postValue(t)
                    Log.d(TAG, "add note onSuccess:${t} ")
                    loading.postValue(false)

//                    runBlocking {
//                        loading.emit(true)
//
//                    }

                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "add note onError:${e} ")
                    loading.postValue(false)

//                    runBlocking {
//                        loading.emit(false)
//
//                    }

                }
            }
        single.subscribe(singleObserver)

    }


    fun updateNoteById(id: Int, noteResponseItem: NoteResponseItem) {
        loading.postValue(true)

        val single: Single<RegisterLoginResponse> =
            NoteAPIManger.getApis()?.updateNote(tinyDB.getString("token"), id, noteResponseItem)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread()) as Single<RegisterLoginResponse>

        val singleObserver: SingleObserver<RegisterLoginResponse> =
            object : SingleObserver<RegisterLoginResponse> {
                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onSuccess(t: RegisterLoginResponse) {
                    Log.d(TAG, "update note onSuccess:${t} ")
                    userLoggInRegisterResponse.postValue(t)

                    loading.postValue(false)

//                    runBlocking {
//                        loading.emit(true)
//
//                    }

                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "update onError:${e} ")
                    loading.postValue(false)

//                    runBlocking {
//                        loading.emit(false)
//
//                    }

                }
            }
        single.subscribe(singleObserver)
    }

    fun deleteNoteById(id: Int) {
        loading.postValue(true)

        val single: Single<RegisterLoginResponse> =
            NoteAPIManger.getApis()?.deleteNote(tinyDB.getString("token"), id)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread()) as Single<RegisterLoginResponse>

        val singleObserver: SingleObserver<RegisterLoginResponse> =
            object : SingleObserver<RegisterLoginResponse> {
                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onSuccess(t: RegisterLoginResponse) {
                    Log.d(TAG, "delete note onSuccess:${t} ")
                    loading.postValue(false)

//                    runBlocking {
//                        loading.emit(true)
//
//                    }

                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "delete onError:${e} ")
                    loading.postValue(false)

//                    runBlocking {
//                        loading.emit(false)
//
//                    }

                }
            }
        single.subscribe(singleObserver)
    }


    fun deleteAllNotesFromApi() {
        loading.postValue(true)

        val single: Single<RegisterLoginResponse> =
            NoteAPIManger.getApis()?.deleteAllNotes(tinyDB.getString("token"))
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread()) as Single<RegisterLoginResponse>

        val singleObserver: SingleObserver<RegisterLoginResponse> =
            object : SingleObserver<RegisterLoginResponse> {
                override fun onSubscribe(d: Disposable) {
                    disposable.add(d)
                }

                override fun onSuccess(t: RegisterLoginResponse) {
                    Log.d(TAG, "delete all notes onSuccess:${t} ")
                    loading.postValue(false)

//                    runBlocking {
//                        loading.emit(true)
//
//                    }

                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "delete onError:${e} ")
                    loading.postValue(false)

//                    runBlocking {
//                        loading.emit(false)
//
//                    }

                }
            }
        single.subscribe(singleObserver)
    }


}
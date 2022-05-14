package com.example.note.Note.UI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.note.Note.Adapters.NotesAdapter
import com.example.note.Note.ViewModels.NoteViewModel
import com.example.note.auth.model.LogInModel
import com.example.note.R
import com.example.note.base.BaseActivity
import com.example.note.databinding.ActivityNotesBinding
import com.example.note.Note.models.NoteModel
import com.example.note.auth.ui.ProfileActivity
import com.hussein.aya.Activities.QuranListen.Intent.NoteIntent
import com.hussein.aya.Activities.QuranListen.State.NoteViewState
import com.muddassir.connection_checker.ConnectionState
import com.muddassir.connection_checker.ConnectivityListener
import com.muddassir.connection_checker.checkConnection
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect


class NotesActivity : BaseActivity(), ConnectivityListener {
    lateinit var binding: ActivityNotesBinding
    private val noteViewModel: NoteViewModel by lazy {
        ViewModelProvider(this).get(
            NoteViewModel::class.java
        )
    }
    lateinit var logInModel: LogInModel
    lateinit var adapter: NotesAdapter
    val TAG: String = "notesssss"
    lateinit var layoutManager: StaggeredGridLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notes)


        checkConnection(this)

        getDataFromIntent()
        inIntRecycleView()
        getNotes()

        clickListeners()

    }

    private fun clickListeners() {
        binding.addNote.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            intent.putExtra("user", logInModel)
            startActivityForResult(intent, 0)

        }
        adapter.openOnClick = { noteResponseItem, i ->
            val intent = Intent(this, AddNoteActivity::class.java)
            intent.putExtra("note", noteResponseItem)
            intent.putExtra("user", logInModel)
            startActivityForResult(intent, 0)

        }

        binding.activityNoteAddToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.sync -> syncNotes()
                R.id.profile -> profile()
                R.id.update -> getNotes()
            }

            true
        }
    }

    private fun renderNotesFromDB() {
        lifecycleScope.launchWhenStarted {
            noteViewModel.dbIntentChannel.send(NoteIntent.getNotesFromDB)
        }
        lifecycleScope.launchWhenStarted {

            noteViewModel.dbViewState.collect {
                when (it) {
                    is NoteViewState.ideal -> {

                    }
                    is NoteViewState.NoteListSuccess -> {
                        if (it.Notes.isNullOrEmpty().not()){
                            binding.noNote.visibility=View.GONE
                            binding.noNoteImage.visibility=View.GONE
                            adapter.changeData(it.Notes)
                        }else{
                            binding.noNote.visibility=View.VISIBLE
                            binding.noNoteImage.visibility=View.VISIBLE
                            adapter.changeData(it.Notes)
                        }

                        binding.progressBar.visibility = View.GONE

                    }
                    is NoteViewState.laoding -> {
                        binding.progressBar.visibility = View.VISIBLE

                    }
                    is NoteViewState.error -> {
                        binding.progressBar.visibility = View.GONE
                        makeToast(this@NotesActivity, it.error, Toast.LENGTH_LONG)
                        Log.d(TAG, "render: ${it.error}")


                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun renderNotesFromApi(updateDB: Boolean = false) {
        lifecycleScope.launchWhenStarted {
            noteViewModel.intentChannel.send(NoteIntent.getNotesFromApi)
        }
        lifecycleScope.launchWhenStarted {

            noteViewModel.viewState.collect {
                when (it) {
                    is NoteViewState.ideal -> {

                    }
                    is NoteViewState.NoteListSuccess -> {
                        // 5- succes to get notes from api
                        if (updateDB) {
                            noteViewModel.addListOfNotesTODataBase(it.Notes)
                        }
                        if (it.Notes.isNullOrEmpty().not()){
                            binding.noNote.visibility=View.GONE
                            binding.noNoteImage.visibility=View.GONE
                            adapter.changeData(it.Notes)
                        }else{
                            binding.noNote.visibility=View.VISIBLE
                            binding.noNoteImage.visibility=View.VISIBLE
                            adapter.changeData(it.Notes)
                        }
                        binding.progressBar.visibility = View.GONE

                    }
                    is NoteViewState.laoding -> {
                        binding.progressBar.visibility = View.VISIBLE

                    }
                    is NoteViewState.error -> {
                        binding.progressBar.visibility = View.GONE
                        makeToast(this@NotesActivity, it.error, Toast.LENGTH_LONG)
                        Log.d(TAG, "render: ${it.error}")
//                        if (isNetworkAvailable()) {
//                            showConfirmationMessage(
//                                R.string.add, R.string.add, R.string.add
//                            ) { dialog: MaterialDialog?, which: DialogAction? -> finish() }
//                        } else {
//                            showConfirmationMessage(
//                                R.string.add, R.string.add, R.string.add
//                            ) { dialog: MaterialDialog?, which: DialogAction? -> finish() }
//                        }

                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun sendAndRenderAddNoteToApi(noteModel: NoteModel) {
        lifecycleScope.launchWhenStarted {
            noteViewModel.addNoteIntentChannel.send(NoteIntent.addNoteToApi(noteModel))
        }
        lifecycleScope.launchWhenStarted {

            noteViewModel.addNoteViewState.collect {
                when (it) {
                    is NoteViewState.ideal -> {

                    }
                    is NoteViewState.addNoteSuccess -> {
                        // 4- success to send the note to api
                        delay(3000)
                        renderNotesFromApi(true)

                        makeToast(this@NotesActivity, it.Notes.noteTitle)
                        binding.progressBar.visibility = View.GONE

                    }
                    is NoteViewState.laoding -> {
                        binding.progressBar.visibility = View.VISIBLE

                    }
                    is NoteViewState.error -> {
                        binding.progressBar.visibility = View.GONE
                        makeToast(this@NotesActivity, it.error, Toast.LENGTH_LONG)
                        Log.d(TAG, "render: ${it.error}")


                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun renderOfflineNotesFromDB() {
        lifecycleScope.launchWhenStarted {
            noteViewModel.offlineNoteIntentChannel.send(NoteIntent.offlineNotesDB)
        }
        lifecycleScope.launchWhenStarted {

            noteViewModel.offlineNoteViewState.collect {
                when (it) {
                    is NoteViewState.ideal -> {

                    }
                    is NoteViewState.NoteListSuccess -> {
                        if (it.Notes.isNullOrEmpty().not()) {
                            for (n in it.Notes) {
                                // 2- send the offline note to api
                                sendAndRenderAddNoteToApi(
                                    NoteModel(
                                        logInModel.email,
                                        n.noteTitle!!,
                                        n.description!!,
                                        n.date!!,
                                        true
                                    )
                                )
                                // 3- delete it
                                noteViewModel.deleteFromDataBaseWithId(n.id!!)
                            }
                        }
                        binding.progressBar.visibility = View.GONE

                    }
                    is NoteViewState.laoding -> {
                        binding.progressBar.visibility = View.VISIBLE

                    }
                    is NoteViewState.error -> {
                        binding.progressBar.visibility = View.GONE
                        makeToast(this@NotesActivity, it.error, Toast.LENGTH_LONG)
                        Log.d(TAG, "render: ${it.error}")
//                        if (isNetworkAvailable()) {
//                            showConfirmationMessage(
//                                R.string.add, R.string.add, R.string.add
//                            ) { dialog: MaterialDialog?, which: DialogAction? -> finish() }
//                        } else {
//                            showConfirmationMessage(
//                                R.string.add, R.string.add, R.string.add
//                            ) { dialog: MaterialDialog?, which: DialogAction? -> finish() }
//                        }

                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun getNotes() {
        if (isNetworkAvailable()) {
            renderNotesFromApi()
            makeToast(this@NotesActivity, "getNotes online")


        } else {
            renderNotesFromDB()
            makeToast(this@NotesActivity, "getNotes offline")


        }

    }


    private fun syncNotes() {
        makeToast(this, "sync")

        if (isNetworkAvailable()) {
            renderNotesFromApi()
            // 1- get offline notes the is online == false
            renderOfflineNotesFromDB()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0) {
            getNotes()
        }
    }

    private fun profile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    private fun deleteAllNotes() {
        noteViewModel.deleteAllNotes()
        noteViewModel.deleteAllNotesFromApi()
    }

    private fun inIntRecycleView() {
        adapter = NotesAdapter(null)
        layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        binding.notesRecycleView.layoutManager = layoutManager
        binding.notesRecycleView.adapter = adapter
    }

    private fun getDataFromIntent() {
        logInModel = intent.getSerializableExtra("user") as LogInModel

    }


    override fun onConnectionState(state: ConnectionState) {
        when (state) {
            ConnectionState.CONNECTED -> {
                syncNotes()
                makeToast(this, "CONNECTED")
            }
            ConnectionState.SLOW -> {
                syncNotes()
                makeToast(this, "SLOW")

            }
            else -> {
                getNotes()
                makeToast(this, "no internet")

            }
        }
    }


}
package com.hussein.note.auth.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hussein.note.auth.model.LogInModel
import com.hussein.note.auth.model.RegisterModel
import com.hussein.note.base.MyApplication
import com.hussein.note.logIn.api.NoteAPIManger
import com.hussein.note.auth.intent.AuthIntent
import com.hussein.note.auth.sate.AuthViewState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    var tinyDB = com.hussein.note.base.TinyDB(MyApplication.appContext)
    private val TAG: String = "nnnnnnnnnnnn"
    val logInIntentChannel = Channel<AuthIntent>(Channel.UNLIMITED)
    val logInViewState = MutableStateFlow<AuthViewState>(AuthViewState.ideal)

    val registerIntentChannel = Channel<AuthIntent>(Channel.UNLIMITED)
    val registerViewState = MutableStateFlow<AuthViewState>(AuthViewState.ideal)

    init {
        process()
    }

    private fun process() {
        viewModelScope.launch {
            registerIntentChannel.consumeAsFlow().collect {
                when (it) {
                    is AuthIntent.RegisterUser -> registerUser(it.registerModel)

                }
            }
        }
        viewModelScope.launch {
            logInIntentChannel.consumeAsFlow().collect {
                when (it) {
                    is AuthIntent.LoginUser -> logInUser(it.logInModel)

                }
            }
        }
    }

    private fun logInUser(logInModel: LogInModel) {
        viewModelScope.launch {
            logInViewState.value = AuthViewState.Loading
            logInViewState.value = try {
                val res = NoteAPIManger.getApis()?.logInMvi(logInModel)
                if (res?.body()?.token != null) {
                    tinyDB.putString("token", "Bearer " + res.body()?.token)

                }
                AuthViewState.Success(res?.body()!!)

            } catch (e: Exception) {
                AuthViewState.Error(e.localizedMessage)
            }
        }
    }

    private fun registerUser(registerModel: RegisterModel) {
        viewModelScope.launch {
            registerViewState.value = AuthViewState.Loading
            registerViewState.value = try {
                val res = NoteAPIManger.getApis()?.registerMvi(registerModel)
                AuthViewState.Success(res?.body()!!)

            } catch (e: Exception) {
                AuthViewState.Error(e.localizedMessage)
            }
        }
    }


}
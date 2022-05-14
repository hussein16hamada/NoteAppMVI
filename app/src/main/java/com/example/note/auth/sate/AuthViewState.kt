package com.example.note.auth.sate

import com.example.note.auth.model.RegisterLoginResponse

sealed class AuthViewState {

    object ideal : AuthViewState()

    data class Success(val Response: RegisterLoginResponse) : AuthViewState()

    data class Error(val error: String) : AuthViewState()

    object Loading : AuthViewState()


}

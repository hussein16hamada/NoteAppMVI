package com.hussein.note.auth.intent

import com.hussein.note.auth.model.LogInModel
import com.hussein.note.auth.model.RegisterModel

sealed class AuthIntent{

  data class RegisterUser (var registerModel: RegisterModel) : AuthIntent()


  data class LoginUser (var logInModel: LogInModel) : AuthIntent()

}

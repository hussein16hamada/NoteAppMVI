package com.example.note.auth.intent

import com.example.note.auth.model.LogInModel
import com.example.note.auth.model.RegisterModel

sealed class AuthIntent{

  data class RegisterUser (var registerModel: RegisterModel) : AuthIntent()


  data class LoginUser (var logInModel: LogInModel) : AuthIntent()

}

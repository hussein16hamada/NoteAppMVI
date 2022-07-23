package com.hussein.note.auth.model

import com.google.gson.annotations.SerializedName

data class RegisterLoginResponse(

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val token: String? = null


)

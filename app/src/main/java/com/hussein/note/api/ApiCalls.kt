package com.hussein.note.logIn.api

import com.hussein.note.Note.models.Responses.NoteResponseItem
import com.hussein.note.auth.model.RegisterLoginResponse
import com.hussein.note.auth.model.LogInModel
import com.hussein.note.Note.models.NoteModel
import com.hussein.note.auth.model.RegisterModel
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

interface NoteApiCalls {
    @Headers("Content-Type: application/json")
    @POST("users/register")
    fun register(
        @Body registerModel: RegisterModel,

        ): Single<RegisterLoginResponse>

    @Headers("Content-Type: application/json")
    @POST("users/register")
   suspend fun registerMvi(
        @Body registerModel: RegisterModel,

        ): Response<RegisterLoginResponse>


    @Headers("Content-Type: application/json")
    @POST("users/login")
    fun logIn(
        @Body logInModel: LogInModel,
        ): Single<RegisterLoginResponse>

    @Headers("Content-Type: application/json")
    @POST("users/login")
    suspend fun logInMvi(
        @Body logInModel: LogInModel,
    ): Response<RegisterLoginResponse>

    @Headers("Content-Type: application/json")
    @GET("notes")
    fun getUserNotes(
        @Header("Authorization") token : String
        ): Single<List<NoteResponseItem>>

    @Headers("Content-Type: application/json")
    @GET("notes")
   suspend fun getUserNotesMvi(
        @Header("Authorization") token : String
    ): Response<List<NoteResponseItem>>

    @Headers("Content-Type: application/json")
    @POST("notes/create")
    suspend fun addNoteMvi(
        @Header("Authorization") token : String,
        @Body noteModel: NoteModel
    ): Response<NoteResponseItem>


    @Headers("Content-Type: application/json")
    @POST("notes/create")
    fun addNote(
        @Header("Authorization") token : String,
        @Body noteModel: NoteModel
    ): Single<NoteResponseItem>

    @Headers("Content-Type: application/json")
    @PUT("notes/update")
    fun updateNote(
        @Header("Authorization") token : String,
        @Query("id") NoteId: Int,
        @Body noteResponseItem: NoteResponseItem

    ): Single<RegisterLoginResponse>

    @Headers("Content-Type: application/json")
    @DELETE("notes/delete")
    fun deleteNote(
        @Header("Authorization") token : String,
        @Query("id") NoteId: Int,
    ): Single<RegisterLoginResponse>

    @Headers("Content-Type: application/json")
    @DELETE("notes/deleteAll")
    fun deleteAllNotes(
        @Header("Authorization") token : String,
    ): Single<RegisterLoginResponse>
}
package com.example.note.Note.models.Responses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NoteResponse(

    @field:SerializedName("NoteResponse")
    val noteResponse: List<NoteResponseItem?>? = null
)

@Entity
data class NoteResponseItem(
    @PrimaryKey(autoGenerate = false)
    @field:SerializedName("id")
    val id: Int? = null,

    @ColumnInfo
    @field:SerializedName("noteTitle")
    val noteTitle: String? = null,

    @ColumnInfo
    @field:SerializedName("description")
    val description: String? = null,

    @ColumnInfo
    @field:SerializedName("date")
    val date: Long? = null,

    @ColumnInfo
    @field:SerializedName("isOnline")
    val isOnline: Boolean? = false

) : Serializable

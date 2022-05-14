package com.example.note.Note.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoteModel(
    @ColumnInfo
    var email: String,
    @ColumnInfo
    var noteTitle: String,
    @ColumnInfo
    var description: String,
    @PrimaryKey(autoGenerate = false)
    var date: Long = System.currentTimeMillis(),
    @ColumnInfo
    val isOnline: Boolean? = false
)

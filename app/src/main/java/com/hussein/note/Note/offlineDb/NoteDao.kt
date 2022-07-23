package com.hussein.note.Note.offlineDb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hussein.note.Note.models.Responses.NoteResponseItem
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNote(noteResponseItem: NoteResponseItem): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNotes(listOfNotes: List<NoteResponseItem>): Completable

    @get:Query("select * from NoteResponseItem;")
    val allNotes: Single<List<NoteResponseItem>>

    @Query("select * from NoteResponseItem;")
    suspend fun allNotesMvi () :List<NoteResponseItem>

    @Query("DELETE FROM NoteResponseItem")
    fun deleteAllNotes(): Completable

    @Query("DELETE  FROM NoteResponseItem WHERE id = :id")
    fun deleteNote(id: Int): Completable

    @Query("DELETE FROM NoteResponseItem WHERE noteTitle = :title")
    fun deleteNoteWithTitle(title: String): Completable

    @Query("select * from NoteResponseItem WHERE isOnline = :isOnline;")
    fun getOfflineNotes(isOnline: Boolean): Single<List<NoteResponseItem>>

    @Query("select * from NoteResponseItem WHERE isOnline = :isOnline;")
    suspend fun getOfflineNotesMvi(isOnline: Boolean): List<NoteResponseItem>
}
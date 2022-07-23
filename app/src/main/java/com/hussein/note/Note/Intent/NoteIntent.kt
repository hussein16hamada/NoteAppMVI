package com.hussein.aya.Activities.QuranListen.Intent

import com.hussein.note.Note.models.NoteModel

sealed class NoteIntent{
  object getNotesFromApi : NoteIntent()
  object getNotesFromDB : NoteIntent()
  data class addNoteToApi (var noteModel: NoteModel) : NoteIntent()
  object offlineNotesDB : NoteIntent()
  object click : NoteIntent()
}

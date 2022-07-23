package com.hussein.aya.Activities.QuranListen.State

import com.hussein.note.Note.models.Responses.NoteResponseItem

sealed class NoteViewState{
  // ideal
    object ideal : NoteViewState()

    data class NoteListSuccess(val Notes: List<NoteResponseItem>): NoteViewState()

    // error
    data class addNoteSuccess(val Notes: NoteResponseItem): NoteViewState()

    data class error(val error: String?): NoteViewState()

    //loading
    object laoding: NoteViewState()


}

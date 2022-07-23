package com.hussein.note.Note.UI

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hussein.note.Note.models.Responses.NoteResponseItem
import com.hussein.note.Note.ViewModels.NoteViewModel
import com.hussein.note.auth.model.LogInModel
import com.hussein.note.Note.models.NoteModel
import com.hussein.note.R
import com.hussein.note.base.BaseActivity
import com.hussein.note.base.MyApplication
import com.hussein.note.base.TinyDB
import com.hussein.note.databinding.ActivityAddNoteBinding
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape


class AddNoteActivity : BaseActivity() {
    lateinit var binding: ActivityAddNoteBinding
    lateinit var noteViewModel: NoteViewModel
    lateinit var logInModel: LogInModel
    var noteResponseItem: NoteResponseItem? = null
    lateinit var noteModel: NoteModel
    lateinit var tinyDB: TinyDB
    var isConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_note)

        inInt()

        getDataFromIntent()

        setupMarkwon()

        isEditNoteScreen()

        clickListener()



    }

    private fun clickListener() {
        binding.activityNoteAddToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.note_add_save -> saveNote()
                R.id.note_delete -> deleteNote()
//                R.id.color -> pickColor()
            }

            true
        }
    }

    private fun pickColor() {
        // Kotlin Code
        ColorPickerDialog
            .Builder(this)        				// Pass Activity Instance
            .setTitle("Pick color")           	// Default "Choose Color"
            .setColorShape(ColorShape.SQAURE)   // Default ColorShape.CIRCLE
            .setColorListener { color, colorHex ->
                tinyDB.putInt("color",color)
            }
            .show()
    }

    private fun isEditNoteScreen() {
        if (noteResponseItem?.id != null) {
//            binding.noteBodyEditText.setText(noteResponseItem!!.description)
            binding.noteBodyEditText.renderMD(noteResponseItem!!.description!!)
            binding.noteTitleEditText.setText(noteResponseItem!!.noteTitle)
            binding.noteDate.setText(convertTime(noteResponseItem!!.date!!))

        }
    }

    private fun getDataFromIntent() {
        logInModel = intent.getSerializableExtra("user") as LogInModel
        noteResponseItem = intent.getSerializableExtra("note") as? NoteResponseItem
    }

    private fun inInt() {
        noteViewModel = ViewModelProvider(this).get(
            NoteViewModel::class.java
        )
        tinyDB=TinyDB(this)
    }

    private fun setupMarkwon() {

        binding.noteBodyEditText.setOnFocusChangeListener { _, hasFocus ->
            binding.noteStylesBar.isVisible = hasFocus
            binding.noteDate.isVisible = !hasFocus
            if (hasFocus) binding.noteBodyEditText.setStylesBar(binding.noteStylesBar)
        }

        binding.noteBodyEditText.taskBoxBackgroundColor =
            ResourcesCompat.getColor(resources, R.color.background, theme)
        binding.noteBodyEditText.taskBoxColor =
            ResourcesCompat.getColor(resources, R.color.primary, theme)
    }

    private fun deleteNote() {
        if (noteResponseItem?.id != null) {
            noteViewModel.deleteNoteById(noteResponseItem?.id!!)
            noteViewModel.deleteFromDataBaseWithId(noteResponseItem?.id!!)
            noteViewModel.loading.observe(this, Observer {
                if (it==true){
                    showProgressBar(R.string.loading)
                }else{
                    hideProgressBar()
                    finish()
                }
            })
            Toast.makeText(
                MyApplication.appContext,
                "done deleting",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addToApiOrDb() {

        if (isNetworkAvailable()) {
            noteModel = NoteModel(
                logInModel.email,
                binding.noteTitleEditText.text.toString(),
                binding.noteBodyEditText.getMD(),
                System.currentTimeMillis(),
                true
            )
            noteViewModel.addNote(noteModel)
            noteViewModel.addNoteMutable.observe(this, Observer {
                makeToast(this,it.noteTitle)

                Log.d("nnn", "addToApiOrDb: ${it} ")

                if (it !=null && it.id !=-1){
                    noteViewModel.addNoteTODataBase(
                        NoteResponseItem(
                            it.id,
                            binding.noteTitleEditText.text.toString(),
                            binding.noteBodyEditText.getMD(),
                            System.currentTimeMillis(),
                            true
                        )
                    )
                }
            })

        } else {
            noteViewModel.addNoteTODataBase(
                NoteResponseItem(
                (-1000 .. -1).random(),
                binding.noteTitleEditText.text.toString(),
                binding.noteBodyEditText.getMD(),
                System.currentTimeMillis(),
                false
            )
            )
        }
    }


    private fun updateToApiOrDb() {
        if (isNetworkAvailable()) {

            noteViewModel.updateNoteById(
                noteResponseItem?.id!!,
                NoteResponseItem(
                    noteResponseItem?.id,
                    binding.noteTitleEditText.text.toString(),
                    binding.noteBodyEditText.getMD(),
                    System.currentTimeMillis(),
                    true
                )
            )

            noteViewModel.addNoteTODataBase(
                NoteResponseItem(
                    noteResponseItem?.id,
                    binding.noteTitleEditText.text.toString(),
                    binding.noteBodyEditText.getMD(),
                    System.currentTimeMillis(),
                    true
                ))
        } else {

            noteViewModel.addNoteTODataBase( NoteResponseItem(
                noteResponseItem?.id,
                binding.noteBodyEditText.getMD(),
                binding.noteBodyEditText.getMD(),
                System.currentTimeMillis(),
                false
            ))
        }
    }

    private fun saveNote() {

        if (noteResponseItem?.id == null) {
            addToApiOrDb()

        } else {

            updateToApiOrDb()
        }

        finish()

    }




}
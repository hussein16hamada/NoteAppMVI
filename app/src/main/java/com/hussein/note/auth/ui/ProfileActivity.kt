package com.hussein.note.auth.ui

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.hussein.note.R
import com.hussein.note.databinding.ActivityProfileBinding
import com.hussein.note.Note.ViewModels.NoteViewModel
import com.hussein.note.auth.model.LogInModel
import com.hussein.note.base.BaseActivity
import com.hussein.note.base.TinyDB

class ProfileActivity : BaseActivity() {
    lateinit var tinyDB: TinyDB
    lateinit var noteViewModel: NoteViewModel
    lateinit var logInModel: LogInModel
    lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding=DataBindingUtil.setContentView(this, R.layout.activity_profile)

        inInt()
        getUserInfo()
        fillUserInfo()
        clickListeners()
    }

    private fun clickListeners() {
        binding.logOut.setOnClickListener {
            noteViewModel.deleteAllNotes()
            tinyDB.putString("token","")
            val intent = Intent(this, LogInActivity::class.java)
            finish()
            startActivity(intent)
        }
    }

    private fun fillUserInfo() {
      binding.useEmail.text=logInModel.email
    }


    private fun getUserInfo() {
        logInModel=tinyDB.getObject("user", LogInModel::class.java)
    }
    private fun inInt() {
        noteViewModel = ViewModelProvider(this).get(
            NoteViewModel::class.java
        )
        tinyDB= TinyDB(this)
    }


}
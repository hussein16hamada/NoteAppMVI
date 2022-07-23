package com.hussein.note.auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hussein.note.R
import com.hussein.note.Note.UI.NotesActivity
import com.hussein.note.base.TinyDB
import com.hussein.note.auth.intent.AuthIntent
import com.hussein.note.auth.model.LogInModel
import com.hussein.note.auth.model.RegisterModel
import com.hussein.note.auth.sate.AuthViewState
import com.hussein.note.auth.viewModel.AuthViewModel
import com.hussein.note.base.BaseActivity
import com.hussein.note.databinding.ActivityLogInBinding
import com.hussein.note.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collect

class LogInActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var logInModel: LogInModel
     private val authViewModel: AuthViewModel by lazy {
        ViewModelProvider(this).get(
            AuthViewModel::class.java
        )
    }
    lateinit var tinyDB: TinyDB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        inInt()
        checkSession()
        clickListeners()


    }

    private fun checkSession() {

        if (tinyDB.getString("token").isNullOrEmpty().not()) {
            val logInModel = tinyDB.getObject("user", LogInModel::class.java)
            val intent = Intent(this, NotesActivity::class.java)
            intent.putExtra("user", logInModel)
            finish()
            startActivity(intent)
        }
    }

    private fun clickListeners() {
        binding.signIn.setOnClickListener {
            if (binding.signIn.text == "Register") {
               // TODO Should do advanced validation here instead of simple one

                val registerModel = RegisterModel(
                        binding.email.editText?.text?.toString()!!,
                        binding.name.editText?.text?.toString()!!,
                        binding.password.editText?.text?.toString()!!
                    )
                if (registerModel.name.isNotEmpty() && registerModel.email.isNotEmpty() &&
                    registerModel.password.isNotEmpty()) {
                    sendAndRenderRegisterUser(registerModel)
                }else{
                    makeToast(this,"All Fields Are Required")
                }

            } else {
                logInModel = LogInModel(
                    binding.email.editText?.text?.toString()!!,
                    binding.password.editText?.text?.toString()!!
                )
//                    // TODO Should do advanced validation here instead of simple one
               if ( logInModel.email.isNotEmpty() && logInModel.password.isNotEmpty()){
                   sendAndRenderLogInUser(logInModel)
               }else{
                   makeToast(this,"All Fields Are Required")

               }

            }


        }
        binding.register.setOnClickListener {
            if (binding.register.text == "sign in") {
                binding.name.visibility = View.GONE
                binding.signIn.text = "sign in"
                binding.register.text = "Register"
            } else {
                binding.name.visibility = View.VISIBLE
                binding.signIn.text = "Register"
                binding.register.text = "sign in"
            }

        }


    }



    private fun sendAndRenderRegisterUser(registerModel: RegisterModel) {
        lifecycleScope.launchWhenStarted {
            authViewModel.registerIntentChannel.send(AuthIntent.RegisterUser(registerModel))
        }
       lifecycleScope.launchWhenStarted {
           authViewModel.registerViewState.collect {
               when (it) {
                   is AuthViewState.ideal -> {
                       binding.progressBar.visibility = View.VISIBLE
                   }
                   is AuthViewState.Success -> {
                       Toast.makeText(
                           this@LogInActivity,
                           "Register Done Successfully",
                           Toast.LENGTH_SHORT
                       ).show()
                       if (it.Response.success == true) {
                           binding.progressBar.visibility = View.GONE
                           if (binding.register.text == "sign in") {
                               binding.name.visibility = View.GONE
                               binding.signIn.text = "sign in"
                               binding.register.text = "Register"
                           } else {
                               binding.name.visibility = View.VISIBLE
                               binding.signIn.text = "Register"
                               binding.register.text = "sign in"
                           }

                       } else {
                           binding.progressBar.visibility = View.GONE
                       }

                   }
                   is AuthViewState.Loading -> {
                       binding.progressBar.visibility = View.VISIBLE

                   }
                   is AuthViewState.Error -> {
                       binding.progressBar.visibility = View.GONE
                       makeToast(this@LogInActivity,it.error)

                   }
                   else -> {

                   }
               }
           }
       }
    }

    private fun sendAndRenderLogInUser(logInModel: LogInModel) {

        lifecycleScope.launchWhenStarted {
            authViewModel.logInIntentChannel.send(AuthIntent.LoginUser(logInModel))
        }
        lifecycleScope.launchWhenStarted {
            authViewModel.logInViewState.collect {
                when (it) {
                    is AuthViewState.ideal -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is AuthViewState.Success -> {
                        Toast.makeText(
                            this@LogInActivity,
                            "Logged In Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        if (it.Response.success == true) {
                            val intent = Intent(this@LogInActivity, NotesActivity::class.java)
                            tinyDB.putObject("user", logInModel)
                            intent.putExtra("user", logInModel)
                            finish()
                            startActivity(intent)

                        } else {
                            binding.progressBar.visibility = View.GONE
                        }

                    }
                    is AuthViewState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE

                    }
                    is AuthViewState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        makeToast(this@LogInActivity,it.error)

                    }
                    else -> {

                    }
                }
            }
        }

    }


    private fun inInt() {

        tinyDB = TinyDB(this)

    }
}
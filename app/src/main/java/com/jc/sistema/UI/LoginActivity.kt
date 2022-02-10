package com.jc.sistema.UI

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.jc.sistema.Models.Users
import com.jc.sistema.Providers.AuthenticationProvider
import com.jc.sistema.Providers.TokenProvider
import com.jc.sistema.UI.Menu.MenuAdmin
import com.jc.sistema.Utils.BaseActivity
import com.jc.sistema.Utils.Constants
import com.jc.sistema.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var mAuth : AuthenticationProvider
    private var token : TokenProvider = TokenProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = AuthenticationProvider()

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.txtRegister.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }

    }

    private fun login() {
        val email : String = binding.edtEmail.text.toString()
        val password : String =  binding.edtPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()){
            showDialog("Iniciando Sesión...")
            mAuth.login(email,password).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val id = task.result!!.user!!.uid
                    mAuth.verifyTypeUser(id).get().addOnSuccessListener { document ->
                        if (document.exists()){
                            val data = document.toObject(Users::class.java)
                            val type = data!!.type_user
                            val fullname = data.full_name
                            val sharedPreferences = getSharedPreferences(Constants.TYPE_USER, Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            val sharedPreferencesEmail = getSharedPreferences(Constants.EMAIL, Context.MODE_PRIVATE)
                            val editorEmail = sharedPreferencesEmail.edit()
                            val sharedPreferencesPassword = getSharedPreferences(Constants.PASSWORD, Context.MODE_PRIVATE)
                            val editorPassword = sharedPreferencesPassword.edit()
                            val sharedPreferencesName = getSharedPreferences(Constants.USERNAME, Context.MODE_PRIVATE)
                            val editorName = sharedPreferencesName.edit()
                            hideDialog()
                            editor.putString(Constants.KEY_TYPE_USER,type)
                            editor.apply()
                            editorEmail.putString(Constants.KEY_EMAIL,email)
                            editorEmail.apply()
                            editorPassword.putString(Constants.KEY_PASSWORD,password)
                            editorPassword.apply()
                            editorName.putString(Constants.USERNAME,fullname)
                            editorName.apply()
                            token.create(id)
                            val intent = Intent(this@LoginActivity, MenuAdmin::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        }

                        else{
                            mAuth.logout()
                            hideDialog()
                            Toast.makeText(this@LoginActivity, "Error!", Toast.LENGTH_LONG).show()
                        }
                    }.addOnCanceledListener {
                        mAuth.logout()
                        hideDialog()
                        Toast.makeText(this@LoginActivity, "Error!", Toast.LENGTH_LONG).show()
                    }.addOnFailureListener {
                        mAuth.logout()
                        hideDialog()
                        Toast.makeText(this@LoginActivity, "Error!", Toast.LENGTH_LONG).show()
                    }

                }
                else{
                    hideDialog()
                    Toast.makeText(this, "Error!, revise sus credenciales", Toast.LENGTH_LONG).show()
                }
            }
        }else{
            Toast.makeText(this, "Complete los campos", Toast.LENGTH_LONG).show()
        }

    }


    override fun onStart() {
        super.onStart()
        if (mAuth.existSession()){
            startActivity(Intent(this,MenuAdmin::class.java))
        }
    }

    override fun onBackPressed() {
    }

}
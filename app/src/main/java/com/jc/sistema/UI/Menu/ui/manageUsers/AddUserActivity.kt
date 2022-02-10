package com.jc.sistema.UI.Menu.ui.manageUsers

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.jc.sistema.Models.DNI
import com.jc.sistema.Models.Users
import com.jc.sistema.Network.InterfaceDNI
import com.jc.sistema.Providers.AuthenticationProvider
import com.jc.sistema.Providers.UserProvider
import com.jc.sistema.R
import com.jc.sistema.Utils.BaseActivity
import com.jc.sistema.Utils.Constants
import com.jc.sistema.databinding.ActivityAddUserBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddUserActivity : BaseActivity() {

    private lateinit var binding : ActivityAddUserBinding
    private var mAuth : AuthenticationProvider = AuthenticationProvider()
    private var mTypeUser : String = ""
    private var mEmail : String = ""
    private var mPassword : String = ""
    private var mUser : UserProvider = UserProvider()

    var URL = "https://dniruc.apisperu.com/api/v1/dni/"
    var BASE_TOKEN = "?token="
    var TOKEN = BASE_TOKEN + "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InNhbWFuLmRhbm55OTVAZ21haWwuY29tIn0.J7apbfAgC6PK_L9EJBkJWMdJmHZZxYWVr2HFEp8WqLQ"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferenceEmail : SharedPreferences = getSharedPreferences(Constants.EMAIL,MODE_PRIVATE)
        mEmail = preferenceEmail.getString(Constants.KEY_EMAIL,"").toString()

        val preferencePassword : SharedPreferences = getSharedPreferences(Constants.PASSWORD,MODE_PRIVATE)
        mPassword = preferencePassword.getString(Constants.KEY_PASSWORD,"").toString()

        val adapterSpinner = ArrayAdapter.createFromResource(this, R.array.type_users,
            R.layout.support_simple_spinner_dropdown_item)
        binding.spUsers.adapter = adapterSpinner
        binding.spUsers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mTypeUser = parent!!.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.edtDni.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 8) {
                    val DNI: String = binding.edtDni.text.toString()
                    searchDni(DNI)
                }
            }
        })

        binding.btnEditDni.setOnClickListener {
            binding.edtDni.isEnabled = true
            binding.edtFullname.setText("")
            binding.edtDni.setText("")
            binding.btnEditDni.visibility = View.GONE
            binding.tilFullname.visibility = View.GONE
        }

        binding.btnRegister.setOnClickListener {
            register()
        }

    }

    private fun searchDni(dni: String) {
        showDialog("Buscando DNI...")
        val gson = GsonBuilder().serializeNulls().create()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val interfaceDNI: InterfaceDNI = retrofit.create(InterfaceDNI::class.java)
        val call: Call<DNI> = interfaceDNI.getDataDni(dni + TOKEN)
        call.enqueue(object : Callback<DNI> {
            override fun onResponse(call: Call<DNI>, response: Response<DNI>) {

                if (!response.isSuccessful) {
                    Toast.makeText(this@AddUserActivity, "Error !", Toast.LENGTH_SHORT)
                        .show()
                    binding.edtDni.setText("")
                    hideDialog()
                }

                else if (response.body()?.apellidoPaterno?.isNotBlank()!!) {
                    var data = ""
                    data = response.body()!!.apellidoPaterno + " " + response.body()!!
                        .apellidoMaterno + " " + response.body()!!.nombres
                    binding.tilFullname.visibility = View.VISIBLE
                    binding.btnEditDni.visibility = View.VISIBLE
                    binding.edtDni.isEnabled = false
                    binding.edtFullname.setText(data)
                    hideDialog()
                } else {
                    binding.edtDni.setText("")
                    hideDialog()
                    Toast.makeText(this@AddUserActivity, "Error de DNI", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<DNI>, t: Throwable) {
                binding.edtDni.setText("")
                hideDialog()
                Toast.makeText(
                    this@AddUserActivity,
                    "Error!, inténtelo mas tarde",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        })
    }

    private fun register(){
        val email : String = binding.edtEmail.text.toString()
        val password : String =  binding.edtPassword.text.toString()
        val dni : String = binding.edtDni.text.toString()
        val fullname : String = binding.edtFullname.text.toString()

        if (mTypeUser != "-" && dni.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && fullname.isNotEmpty()){
            showDialog("Registrando Usuario...")
            mAuth.register(email,password).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val id = task.result!!.user!!.uid
                    val data = Users(id,dni,fullname,"",mTypeUser,email,password)
                    mUser.create(data).addOnCompleteListener { response ->
                        if (response.isSuccessful) {
                            mAuth.logout()
                            mAuth.login(mEmail,mPassword).addOnCompleteListener {
                                if (it.isSuccessful){
                                    hideDialog()
                                    finish()
                                    Toast.makeText(this, "Usuario creado!", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                        else{
                            hideDialog()
                            Toast.makeText(this, "Error al guardar datos!", Toast.LENGTH_LONG).show()
                        }
                    }.addOnCanceledListener{
                        hideDialog()
                        Toast.makeText(this, "Error al guardar datos!", Toast.LENGTH_LONG).show()
                    }.addOnFailureListener {
                        hideDialog()
                        Toast.makeText(this, "Error al guardar datos!", Toast.LENGTH_LONG).show()
                    }
                }else{
                    hideDialog()
                    Toast.makeText(this, "Error al guardar datos!", Toast.LENGTH_LONG).show()
                }
            }.addOnCanceledListener {
                hideDialog()
                Toast.makeText(this, "Error al guardar datos!", Toast.LENGTH_LONG).show()

            }.addOnFailureListener {
                hideDialog()
                Toast.makeText(this, "Error al guardar datos!", Toast.LENGTH_LONG).show()

            }
        }

    }

}
package com.jc.sistema.Providers

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase

class TokenProvider {

    var mDatabase: DatabaseReference = Firebase.database.reference.child("Tokens")

    fun create(idUser: String) {
        FirebaseInstallations.getInstance().getToken(true)
            .addOnCompleteListener { instanceIdResult ->
                val token = instanceIdResult.result!!.token
                mDatabase.child(idUser).setValue(token)
            }
    }

    fun getToken(idUser: String): DatabaseReference {
        return mDatabase.child(idUser)
    }

    // LLAMA A ESTE METODO CUANDO EL USUARIO CIERRE SESION
    fun deleteToken(idUser: String) {
        mDatabase.child(idUser).removeValue()
    }
}
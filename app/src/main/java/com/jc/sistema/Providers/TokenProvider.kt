package com.jc.sistema.Providers


import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.jc.sistema.Utils.Constants
import com.jc.sistema.Models.Token

class TokenProvider {

    var mFirebase : FirebaseFirestore = FirebaseFirestore.getInstance()

    fun create(id: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            // Get new FCM registration token
            val token = Token(task.result.toString())
            mFirebase.collection(Constants.TOKEN).document(id).set(token)
        }
    }

    fun getToken(idUser: String): DocumentReference {
        return mFirebase.collection(Constants.TOKEN).document(idUser)
    }

    // LLAMA A ESTE METODO CUANDO EL USUARIO CIERRE SESION
    fun deleteToken(idUser: String) {
        mFirebase.collection(Constants.TOKEN).document(idUser).delete()
    }
}
package com.jc.sistema.Providers

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jc.sistema.Utils.Constants

class AuthenticationProvider {

    var mAuth: FirebaseAuth = Firebase.auth
    var mDatabaseReference : DatabaseReference = Firebase.database.reference

    fun register(email: String, password: String): Task<AuthResult?> {
        return mAuth.createUserWithEmailAndPassword(email, password)
    }

    fun login(email: String, password: String): Task<AuthResult?> {
        return mAuth.signInWithEmailAndPassword(email, password)
    }

    fun verifyTypeUser(id : String) : DatabaseReference {
        return mDatabaseReference.child(Constants.USERS).child(id)
    }

    fun logout() {
        mAuth.signOut()
    }

    fun getId(): String {
        return mAuth.currentUser!!.uid
    }

    fun existSession(): Boolean {
        var exist = false
        if (mAuth.currentUser != null) {
            exist = true
        }
        return exist
    }

}
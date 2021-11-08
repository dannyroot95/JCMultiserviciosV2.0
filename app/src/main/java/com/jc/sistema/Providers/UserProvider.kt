package com.jc.sistema.Providers

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jc.sistema.Models.Users
import com.jc.sistema.Utils.Constants
import java.util.HashMap

class UserProvider {
    var mDatabase: DatabaseReference = Firebase.database.reference
        .child(Constants.USERS)

    fun create(user: Users): Task<Void> {
        val map: MutableMap<String, Any> = HashMap()
        map["id"] = user.id
        map["dni"] = user.dni
        map["full_name"] = user.full_name
        map["type_user"] = user.type_user
        map["email"] = user.email
        return mDatabase.child(user.id).setValue(map)
    }

    fun getClient(idAdmin: String): DatabaseReference {
        return mDatabase.child(idAdmin)
    }

}
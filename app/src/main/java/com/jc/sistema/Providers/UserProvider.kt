package com.jc.sistema.Providers

import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.jc.sistema.Models.Users
import com.jc.sistema.UI.Menu.ui.manageUsers.ManageUsersFragment
import com.jc.sistema.Utils.Constants

class UserProvider {
    var mDatabase = FirebaseFirestore.getInstance()

    fun create(user: Users): Task<Void> {
        return mDatabase.collection(Constants.USERS).document(user.id).set(user, SetOptions.merge())
    }

    fun getUserList(fragment : ManageUsersFragment){
        mDatabase.collection(Constants.USERS).get().addOnSuccessListener{ task ->
                val userList: ArrayList<Users> = ArrayList()
                for (i in task.documents){
                    val user = i.toObject(Users::class.java)!!
                    userList.add(user)
                }
                fragment.successUserList(userList)
            }
    }

    fun getClient(idAdmin: String): DocumentReference {
        return mDatabase.collection(Constants.USERS).document(idAdmin)
    }

}
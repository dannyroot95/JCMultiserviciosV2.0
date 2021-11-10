package com.jc.sistema.Providers

import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.jc.sistema.Models.Users
import com.jc.sistema.UI.Menu.ui.manageUsers.ManageUsersFragment
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

    fun getUserList(fragment : ManageUsersFragment , id : String){
        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val userList: ArrayList<Users> = ArrayList()
                    for (i in snapshot.children){
                        val user = i.getValue(Users::class.java)!!
                        userList.add(user)
                    }
                    fragment.successUserList(userList)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(fragment.context,"Error de server",Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getClient(idAdmin: String): DatabaseReference {
        return mDatabase.child(idAdmin)
    }

}
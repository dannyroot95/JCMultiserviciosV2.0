package com.jc.sistema.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Users (var id : String = ""  ,
                  var dni : String = "",
                   var full_name : String = "" ,
                   var image : String = "",
                   var type_user : String = "",
                   var email : String = "" ,
                   var password : String = "") : Parcelable

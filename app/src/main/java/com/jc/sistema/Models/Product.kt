package com.jc.sistema.Models
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Product(val id : String = "",
                   val image : String = "",
                   val description : String = "",
                   val category : String = "",
                   val code : String = "",
                   val arrayColors : ArrayList<Colors> = ArrayList(),
                   val price_for_pza : String = "",
                   val price_for_doc : String = "",
                   val price_for_jgo : String = "",
                   val stock : String = "") : Parcelable , Serializable

@Parcelize
data class Colors (
    val value : Int = 0,
    var quantity : Int = 0) : Parcelable , Serializable
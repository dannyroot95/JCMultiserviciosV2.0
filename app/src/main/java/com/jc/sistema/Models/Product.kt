package com.jc.sistema.Models
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(val id : String = "",
                   val image : String = "",
                   val description : String = "",
                   val category : String = "",
                   val code : String = "",
                   val colors : Colors = Colors(),
                   val price_for_pza : String = "",
                   val price_for_doc : String = "",
                   val price_for_jgo : String = "",
                   val stock : String = "") : Parcelable

@Parcelize
data class Colors (
    val rojo : Int = 0,
    val verde : Int = 0,
    val blanco : Int = 0) : Parcelable
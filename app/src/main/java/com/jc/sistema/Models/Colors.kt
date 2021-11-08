package com.jc.sistema.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Colors (val rojo : Int = 0, val verde : Int = 0 , val blanco : Int = 0) : Parcelable
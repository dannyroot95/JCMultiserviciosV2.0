package com.jc.sistema.Models


data class Cart (val product_id : String = "",
                 val image : String = "",
                 val description : String = "",
                 val price : String = "",
                 val total_price : String = "",
                 val arrayColors : ArrayList<Colors> = ArrayList(),
                 val valueSelected : String = "",
                 val quantity : String =  "")
package com.jc.sistema.Models

data class Order(val id : String = "" ,
                 val timestamp : Long = 0L,
                 val num_order : String = "" ,
                 val dni : String = "" ,
                 val client_name : String = "",
                 val status : String = "")

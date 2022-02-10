package com.jc.sistema.Providers

import com.google.firebase.firestore.FirebaseFirestore
import com.jc.sistema.Models.Order
import com.jc.sistema.UI.Menu.ui.orders.OrdersFragment
import com.jc.sistema.Utils.Constants

class OrderProvider {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun getAllOrderList(fragment : OrdersFragment){
        mFireStore.collection(Constants.ORDERS).get().addOnSuccessListener { snapshot ->
            val orderList: ArrayList<Order> = ArrayList()
            for (i in snapshot.documents){
                val order = i.toObject(Order::class.java)!!
                orderList.add(order)
            }
            fragment.successOrderList(orderList)
        }
    }

}
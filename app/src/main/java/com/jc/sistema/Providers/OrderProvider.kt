package com.jc.sistema.Providers

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.jc.sistema.Models.Order
import com.jc.sistema.Models.Product
import com.jc.sistema.UI.Menu.ui.orders.OrdersFragment
import com.jc.sistema.UI.Menu.ui.products.ProductFragment
import com.jc.sistema.Utils.Constants
import com.jc.sistema.Utils.TinyDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

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

   fun fetchData(fragment : Fragment){
        val db = TinyDB(fragment.requireContext())

       mFireStore.collection(Constants.PRODUCTS).addSnapshotListener { snapshot, _ ->
           val productList: ArrayList<Product> = ArrayList()
           for (i in snapshot!!.documents){
               val product = i.toObject(Product::class.java)!!
               val path: String = fragment.requireContext().getExternalFilesDir(null).toString()+"/"+product.id+".jpg"
               val file = File(path)
               if (file.exists()){
                   productList.add(product)
               }else{
                   getBitmapFromURL(product.image,fragment.requireContext(),product.id)
                   productList.add(product)
               }
           }
           db.putListProduct(Constants.PRODUCTS,productList)
       }


    }

    private fun getBitmapFromURL(url: String, context: Context, name: String) {
        CoroutineScope(Job() + Dispatchers.IO).launch {
            val mFolder = File(context.getExternalFilesDir(null), "$name.jpg")
            val storageRef: StorageReference = Firebase.storage.getReferenceFromUrl(url)
            storageRef.getFile(mFolder).addOnSuccessListener {
            }
        }
    }


}
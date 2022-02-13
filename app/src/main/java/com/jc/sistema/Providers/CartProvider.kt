package com.jc.sistema.Providers

import android.app.Activity
import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.jc.sistema.Models.Cart
import com.jc.sistema.Models.Order
import com.jc.sistema.Models.Product
import com.jc.sistema.UI.Menu.ui.orders.CartListActivity
import com.jc.sistema.Utils.Constants
import com.jc.sistema.Utils.TinyDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class CartProvider {

    private val mFireStore = FirebaseFirestore.getInstance()


    fun getListCartItems(activity: CartListActivity){
        mFireStore.collection(Constants.CART_ITEMS).addSnapshotListener { snapshot , _ ->
            val cartList: ArrayList<Cart> = ArrayList()
            for (i in snapshot!!.documents){
                val cart = i.toObject(Cart::class.java)!!
                cartList.add(cart)
            }
            activity.successCartList(cartList)
        }
    }

    fun fetchDataActivity(activity: Activity){
        val db = TinyDB(activity)
        mFireStore.collection(Constants.PRODUCTS).addSnapshotListener { snapshot , _ ->
            val productList: ArrayList<Product> = ArrayList()
            for (i in snapshot!!.documents){
                val product = i.toObject(Product::class.java)!!
                val path: String = activity.getExternalFilesDir(null).toString()+"/"+product.id+".jpg"
                val file = File(path)
                if (file.exists()){
                    productList.add(product)
                }else{
                    getBitmapFromURL(product.image,activity,product.id)
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
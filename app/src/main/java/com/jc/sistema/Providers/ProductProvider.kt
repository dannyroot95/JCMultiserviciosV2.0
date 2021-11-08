package com.jc.sistema.Providers

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.jc.sistema.Models.Product
import com.jc.sistema.UI.Menu.ui.products.AddProductActivity
import com.jc.sistema.UI.Menu.ui.products.ProductFragment
import com.jc.sistema.Utils.Constants

class ProductProvider {

    var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
        .child(Constants.PRODUCTS)

    private val mFireStore = FirebaseFirestore.getInstance()

    fun create(product: Product, activity : AddProductActivity, code : String){
        mFireStore.collection(Constants.PRODUCTS).document(code).set(product, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
        }.addOnFailureListener {
                activity.hideDialog()
                Toast.makeText(activity,"ERROR!",Toast.LENGTH_SHORT).show()
            }.addOnCanceledListener {
                activity.hideDialog()
                Toast.makeText(activity,"ERROR!",Toast.LENGTH_SHORT).show()
            }
    }


    fun geProductList(fragment : ProductFragment){
        mFireStore.collection(Constants.PRODUCTS).get().addOnSuccessListener { snapshot ->
            val productList: ArrayList<Product> = ArrayList()
            for (i in snapshot.documents){
                val product = i.toObject(Product::class.java)!!
              productList.add(product)
            }
            fragment.successProductList(productList)
        }
    }

    fun getProduct(id: String): DatabaseReference {
        return mDatabase.child(id)
    }

}
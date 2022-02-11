package com.jc.sistema.Providers

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.jc.sistema.Models.Product
import com.jc.sistema.UI.Menu.ui.products.AddProductActivity
import com.jc.sistema.UI.Menu.ui.products.EditProductActivity
import com.jc.sistema.UI.Menu.ui.products.ProductFragment
import com.jc.sistema.Utils.Constants
import com.jc.sistema.Utils.TinyDB
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class ProductProvider {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun create(product: Product, activity : Activity, code : String){
        mFireStore.collection(Constants.PRODUCTS).document(code).set(product, SetOptions.merge())
            .addOnSuccessListener {
                when(activity) {
                    is AddProductActivity ->{
                        activity.productUploadSuccess()
                    }
                    is EditProductActivity ->{
                        activity.productUploadSuccess()
                    }
                }

        }.addOnFailureListener {
                when(activity) {
                    is AddProductActivity ->{
                        activity.hideDialog()
                        Toast.makeText(activity,"ERROR!",Toast.LENGTH_SHORT).show()
                    }
                    is EditProductActivity ->{
                        activity.hideDialog()
                        Toast.makeText(activity,"ERROR!",Toast.LENGTH_SHORT).show()
                    }
                }

            }.addOnCanceledListener {
                when(activity) {
                    is AddProductActivity ->{
                        activity.hideDialog()
                        Toast.makeText(activity,"ERROR!",Toast.LENGTH_SHORT).show()
                    }
                    is EditProductActivity ->{
                        activity.hideDialog()
                        Toast.makeText(activity,"ERROR!",Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    fun geProductList(fragment : ProductFragment){

        val db = TinyDB(fragment.requireContext())

        if (db.getListProduct(Constants.PRODUCTS,Product::class.java).isNotEmpty()){
            val productList: ArrayList<Product> = db.getListProduct(Constants.PRODUCTS,Product::class.java)
            fragment.successProductList(productList)
            fetchData(fragment)
        }

        else{
            fetchData(fragment)
        }

    }

    private fun fetchData(fragment : ProductFragment){
        val db = TinyDB(fragment.requireContext())
        mFireStore.collection(Constants.PRODUCTS).get().addOnSuccessListener { snapshot ->
            val productList: ArrayList<Product> = ArrayList()
            for (i in snapshot.documents){
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
            fragment.successProductList(productList)
        }
    }

    fun getProduct(id: String): DocumentReference {
        return mFireStore.collection(Constants.PRODUCTS).document(id)
    }

    private fun getBitmapFromURL(url: String, context: Context, name: String) {
        CoroutineScope(Job() + Dispatchers.IO).launch {
            val mFolder: File = File(context.getExternalFilesDir(null), "$name.jpg")
            val storageRef: StorageReference = Firebase.storage.getReferenceFromUrl(url)
            storageRef.getFile(mFolder).addOnSuccessListener {
            }
        }
    }

}
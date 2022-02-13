package com.jc.sistema.UI.Menu.ui.orders

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.zxing.integration.android.IntentIntegrator
import com.jc.sistema.Adapters.CartItemsAdapter
import com.jc.sistema.Adapters.ProductAdapter
import com.jc.sistema.Models.Cart
import com.jc.sistema.Models.Product
import com.jc.sistema.Providers.CartProvider
import com.jc.sistema.Providers.OrderProvider
import com.jc.sistema.R
import com.jc.sistema.Utils.BaseActivity
import com.jc.sistema.Utils.CaptureCodeBar
import com.jc.sistema.Utils.Constants
import com.jc.sistema.Utils.TinyDB
import com.jc.sistema.databinding.ActivityCartListBinding
import com.jc.sistema.databinding.DialogProductBinding
import com.squareup.picasso.Picasso
import java.io.File

class CartListActivity : BaseActivity() {

    private lateinit var binding : ActivityCartListBinding
    private lateinit var bindingResultProduct : DialogProductBinding
    private lateinit var mDialog : Dialog
    private lateinit var db : TinyDB
    private lateinit var list : ArrayList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        bindingResultProduct = DialogProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        CartProvider().getListCartItems(this)
        CartProvider().fetchDataActivity(this)
        db = TinyDB(this)
        list = db.getListProduct(Constants.PRODUCTS,Product::class.java)
        mDialog = Dialog(this)
        //mDialog.window!!.setBackgroundDrawableResource(0)
        mDialog.setContentView(bindingResultProduct.root)

        binding.btnSearchProduct.setOnClickListener {
            initScanner()
        }

    }

    private fun initScanner(){
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Scaneando...")
        integrator.captureActivity = CaptureCodeBar::class.java
        integrator.setOrientationLocked(false)
        integrator.setBeepEnabled(true)
        integrator.initiateScan()
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (scanResult != null) {
            if (scanResult.contents == null) {
               // Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show()
            } else {
                var productResult = Product()
                var result = 0
                for (i in list){
                    if (i.code == scanResult.contents){
                        result = 1
                        productResult = i
                    }
                }
                if (result == 1){

                    val path: String = getExternalFilesDir(null).toString()+"/"+productResult.id+".jpg"
                    val file = File(path)
                    val imageUri: Uri = Uri.fromFile(file)

                    if (file.exists()){
                        Picasso.with(this).load(imageUri).into(bindingResultProduct.dgProduct)
                    }else{
                        Picasso.with(this).load(productResult.image).into(bindingResultProduct.dgProduct)
                    }
                    bindingResultProduct.dgDescription.text = productResult.description
                    bindingResultProduct.dgStock.text = "Stock : "+productResult.stock

                    verifyPrices(productResult,bindingResultProduct)


                    mDialog.show()
                }else{
                    Toast.makeText(this,"No existe este producto!",Toast.LENGTH_SHORT).show()
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun verifyPrices(
        productResult: Product,
        bindingResultProduct: DialogProductBinding
    ) {

        var valueSelected = ""
        var price = ""

        if (productResult.price_for_pza != ""){
            bindingResultProduct.lnPriceForPza.visibility = View.VISIBLE
            bindingResultProduct.dgPriceForPza.text = productResult.price_for_pza
            bindingResultProduct.ckPricePza.setOnClickListener {
                if (bindingResultProduct.ckPricePza.isChecked){
                    price = bindingResultProduct.dgPriceForPza.text.toString()
                    valueSelected = "Pieza(s)"
                    bindingResultProduct.txtQuantity.text = "1"
                    bindingResultProduct.lnAddProduct.visibility = View.VISIBLE
                    bindingResultProduct.ckPriceDoc.isEnabled = false
                    bindingResultProduct.ckPriceJgo.isEnabled = false
                }else{
                    price = ""
                    valueSelected = ""
                    bindingResultProduct.lnAddProduct.visibility = View.GONE
                    bindingResultProduct.ckPriceDoc.isEnabled = true
                    bindingResultProduct.ckPriceJgo.isEnabled = true
                }
            }
        }
        if (productResult.price_for_doc != ""){
            bindingResultProduct.lnPriceDoc.visibility = View.VISIBLE
            bindingResultProduct.dgPriceForDoc.text = productResult.price_for_doc
            bindingResultProduct.ckPriceDoc.setOnClickListener {
                if (bindingResultProduct.ckPriceDoc.isChecked){
                    price = bindingResultProduct.dgPriceForDoc.text.toString()
                    valueSelected = "Docena(s)"
                    bindingResultProduct.txtQuantity.text = "1"
                    bindingResultProduct.lnAddProduct.visibility = View.VISIBLE
                    bindingResultProduct.ckPricePza.isEnabled = false
                    bindingResultProduct.ckPriceJgo.isEnabled = false
                }
                else{
                    price = ""
                    valueSelected = ""
                    bindingResultProduct.lnAddProduct.visibility = View.GONE
                    bindingResultProduct.ckPricePza.isEnabled = true
                    bindingResultProduct.ckPriceJgo.isEnabled = true
                }
            }
        }
        if (productResult.price_for_jgo != ""){
            bindingResultProduct.lnPriceForJgo.visibility = View.VISIBLE
            bindingResultProduct.dgPriceForJgo.text = productResult.price_for_jgo
            bindingResultProduct.ckPriceJgo.setOnClickListener {
                if (bindingResultProduct.ckPriceJgo.isChecked){
                    price = bindingResultProduct.dgPriceForJgo.text.toString()
                    valueSelected = "Juego(s)"
                    bindingResultProduct.txtQuantity.text = "1"
                    bindingResultProduct.lnAddProduct.visibility = View.VISIBLE
                    bindingResultProduct.ckPricePza.isEnabled = false
                    bindingResultProduct.ckPriceDoc.isEnabled = false
                }else{
                    price = ""
                    valueSelected = ""
                    bindingResultProduct.lnAddProduct.visibility = View.GONE
                    bindingResultProduct.ckPricePza.isEnabled = true
                    bindingResultProduct.ckPriceDoc.isEnabled = true
                }
            }
        }

        bindingResultProduct.btnSubtract.setOnClickListener {
            val quantity = bindingResultProduct.txtQuantity.text.toString().toInt()
            if (quantity > 1){
                val substract = quantity - 1
                bindingResultProduct.txtQuantity.text = substract.toString()
            }
        }

        bindingResultProduct.btnAdd.setOnClickListener {
            val quantity = bindingResultProduct.txtQuantity.text.toString().toInt()
            val add = quantity + 1
            bindingResultProduct.txtQuantity.text = add.toString()
        }

        bindingResultProduct.btnAddToCart.setOnClickListener {
            val mFirestore = FirebaseFirestore.getInstance()
            if (valueSelected != "" && price != ""){

                val totalPrice = price.toInt()*bindingResultProduct.txtQuantity.text.toString().toInt()

                showDialog("Añadiendo al carrito...")

                val cart = Cart(productResult.id,
                    productResult.image,
                    productResult.description,
                    price,
                    totalPrice.toString(),
                    productResult.arrayColors,
                    valueSelected,
                    bindingResultProduct.txtQuantity.text.toString())

                mFirestore.collection(Constants.CART_ITEMS).document().set(cart,SetOptions.merge()).addOnCompleteListener {
                    if (it.isSuccessful){
                        hideDialog()
                        mDialog.dismiss()
                        Toast.makeText(this,"Agregado!",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        hideDialog()
                        Toast.makeText(this,"Error , intentelo de nuevo!",Toast.LENGTH_SHORT).show()
                    }
                }

            }else{
                Toast.makeText(this,"Seleccione al menos una opción",Toast.LENGTH_SHORT).show()
            }


        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun successCartList(cartList: ArrayList<Cart>) {
        if (cartList.size > 0) {
            binding.rvCart.visibility = View.VISIBLE
            binding.lnNoItemsCart.visibility = View.GONE
            binding.lnProgress.visibility = View.GONE

            binding.rvCart.layoutManager = LinearLayoutManager(this)
            binding.rvCart.setHasFixedSize(true)

            val adapter = CartItemsAdapter(this, cartList)
            binding.rvCart.adapter = adapter
        }else {
            binding.lnProgress.visibility = View.GONE
            binding.rvCart.visibility = View.GONE
            binding.lnNoItemsCart.visibility = View.VISIBLE
        }
    }

}
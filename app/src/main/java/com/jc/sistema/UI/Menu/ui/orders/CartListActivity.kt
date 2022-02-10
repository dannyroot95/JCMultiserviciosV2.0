package com.jc.sistema.UI.Menu.ui.orders

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.zxing.integration.android.IntentIntegrator
import com.jc.sistema.Models.Product
import com.jc.sistema.R
import com.jc.sistema.Utils.BaseActivity
import com.jc.sistema.Utils.CaptureCodeBar
import com.jc.sistema.Utils.Constants
import com.jc.sistema.databinding.ActivityCartListBinding
import com.squareup.picasso.Picasso

class CartListActivity : BaseActivity() {

    private lateinit var binding : ActivityCartListBinding
    private var mFirestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var mDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDialog = Dialog(this)
        mDialog.setContentView(R.layout.dialog_product)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (scanResult != null) {
            if (scanResult.contents == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show()
            } else {
                mFirestore.collection(Constants.PRODUCTS).document(scanResult.contents).get().addOnSuccessListener {
                    if (it.exists()){
                        val data = it.toObject<Product>()!!
                        val image = mDialog.findViewById(R.id.dg_product) as ImageView
                        val name = mDialog.findViewById(R.id.dg_description) as TextView
                        val stock = mDialog.findViewById(R.id.dg_stock) as TextView
                        Picasso.with(this).load(data.image).into(image)
                        name.text = data.description
                        stock.text = "Stock : "+data.stock
                        mDialog.show()
                    }
                    else{
                        Toast.makeText(this,"No existe producto",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}
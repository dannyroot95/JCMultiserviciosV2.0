package com.jc.sistema.UI.Menu.ui.products

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.zxing.integration.android.IntentIntegrator
import com.jc.sistema.Adapters.ColorAdapter
import com.jc.sistema.Models.Colors
import com.jc.sistema.Models.Product
import com.jc.sistema.Providers.ImageProvider
import com.jc.sistema.Providers.ProductProvider
import com.jc.sistema.R
import com.jc.sistema.Utils.BaseActivity
import com.jc.sistema.Utils.CaptureCodeBar
import com.jc.sistema.Utils.Constants
import com.jc.sistema.Utils.FileUtil
import com.jc.sistema.databinding.ActivityAddProductBinding
import kotlinx.android.synthetic.main.activity_add_product.*
import top.defaults.colorpicker.ColorPickerPopup
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class AddProductActivity : BaseActivity() {

    private lateinit var binding : ActivityAddProductBinding
    private var mImageFileProfile: File? = null
    private lateinit var mImageProvider: ImageProvider
    private lateinit var mProductProvider: ProductProvider
    private var category : String  = ""
    var urlImageProduct = ""
    private var adapter =  ColorAdapter(this, emptyList())
    private var items = ArrayList<Colors>()
    private var valueColor : Int = 0


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        items = ArrayList<Colors>()
        valueColor = 0

        binding.rvColors.layoutManager = LinearLayoutManager(this)
        binding.rvColors.setHasFixedSize(true)
        adapter = ColorAdapter(this, items)
        binding.rvColors.adapter = adapter

        mImageProvider = ImageProvider("products")
        mProductProvider = ProductProvider()

        val adapterSpinnerCategory = ArrayAdapter.createFromResource(
            this,
            R.array.category,
            R.layout.support_simple_spinner_dropdown_item
        )
        binding.spCategory.adapter = adapterSpinnerCategory
        binding.spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                category = parent!!.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}}

        val checkColor = binding.chkColor

        if (!checkColor.isChecked) {
            binding.tilStock.visibility = View.VISIBLE

        }

        checkColor.setOnClickListener {

            if (checkColor.isChecked) {
                binding.linearColors.visibility = View.VISIBLE
                binding.tilStock.isEnabled = false
                binding.lnRvColors.visibility = View.VISIBLE
                binding.btnPickColor.setBackgroundColor(0)

            } else {
                items.clear()
                binding.tilStock.isEnabled = true
                valueColor = 0
                binding.edtStockColor.setText("")
                binding.edtStock.setText("")
                binding.btnPickColor.setBackgroundColor(0)
                binding.linearColors.visibility = View.GONE
                binding.tilStock.visibility = View.VISIBLE
                binding.lnRvColors.visibility = View.GONE
                adapter.notifyDataSetChanged()
            }
        }


        val checkPricePZA = binding.chkPricePza
        checkPricePZA.setOnClickListener {
            if (checkPricePZA.isChecked){
                binding.tilPriceForPza.visibility = View.VISIBLE
            }else{binding.tilPriceForPza.visibility = View.GONE}
        }

        val checkPriceDOC = binding.chkPriceDoc
        checkPriceDOC.setOnClickListener {
            if (checkPriceDOC.isChecked){
                binding.tilPriceForDoc.visibility = View.VISIBLE
            }else{binding.tilPriceForDoc.visibility = View.GONE}
        }


        val checkPriceJGO = binding.chkPriceJgo
        checkPriceJGO.setOnClickListener {
            if (checkPriceJGO.isChecked){
                binding.tilPriceForJgo.visibility = View.VISIBLE
            }else{binding.tilPriceForJgo.visibility = View.GONE}
        }

        binding.btnScanCode.setOnClickListener {
            initScanner()
        }
        binding.ivAddImageProduct.setOnClickListener {
            openGallery()
        }
        binding.btnRegisterProduct.setOnClickListener {
            register(items)
        }
        binding.btnPickColor.setOnClickListener {
            ColorPickerPopup.Builder(this)
                .initialColor(Color.RED)
                .enableBrightness(true)
                .enableAlpha(true)
                .okTitle("Elegir")
                .cancelTitle("Cancelar")
                .build()
                .show(it,object : ColorPickerPopup.ColorPickerObserver {
                    override fun onColor(color: Int, fromUser: Boolean) {
                    }
                    override fun onColorPicked(color: Int) {
                        binding.btnPickColor.setBackgroundColor(color)
                        valueColor = color
                        //Toast.makeText(this@AddProductActivity,color.toString(),Toast.LENGTH_SHORT).show()
                    }

                })
        }
        binding.btnAdd.setOnClickListener {
            if (binding.edtStockColor.text.toString() != ""){
                var sum = 0
                val quantity = binding.edtStockColor.text.toString().toInt()
                val data = Colors(valueColor,quantity)
                items.add(data)
                adapter.notifyDataSetChanged()

                for(i in items){
                    sum += i.quantity
                }

                binding.edtStock.setText(sum.toString())

            }else{
                Toast.makeText(this,"Ingrese la cantidad",Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun openGallery() {
        if (binding.edtDescription.text.toString().isNotEmpty()){
            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, Constants.GALLERY_REQUEST)
        }
        else{
            Toast.makeText(this, "Ingrese el nombre del producto...", Toast.LENGTH_SHORT).show()
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

        if (requestCode == Constants.GALLERY_REQUEST && resultCode == RESULT_OK) {
            try {
                showDialog("Subiendo foto...")
                mImageFileProfile = FileUtil().from(this, Objects.requireNonNull(data)!!.data!!)
                mImageProvider.saveImage(
                    this,
                    mImageFileProfile!!,
                    binding.edtDescription.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        mImageProvider.getStorage().downloadUrl.addOnSuccessListener { uri ->
                            urlImageProduct = uri.toString()
                            binding.ivProductImage.setImageBitmap(
                                BitmapFactory.decodeFile(
                                    mImageFileProfile!!.absolutePath
                                )
                            )
                            hideDialog()
                            Toast.makeText(this, "Imagen subida!", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        hideDialog()
                        Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                    }
                }.addOnCanceledListener {
                    hideDialog()
                    Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    hideDialog()
                    Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                }
            }catch (e: Exception){
                hideDialog()
                Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show()
            }
        }

        if (scanResult != null) {
            if (scanResult.contents == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show()
            } else {
                binding.edtCodeProduct.setText(scanResult.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }



    private fun register(list : ArrayList<Colors>) {
        val description = binding.edtDescription.text.toString()
        val code = binding.edtCodeProduct.text.toString()
        val priceForPza = binding.edtPriceForPza.text.toString()
        val priceForDoc = binding.edtPriceForDoc.text.toString()
        val priceForJgo = binding.edtPriceForJgo.text.toString()

        val sum   : Int

        var stock = binding.edtStock.text.toString()

        if (binding.chkColor.isChecked){
            //stock = sum.toString()
        }

       // val color = Colors(red, green, white)

        if (urlImageProduct != "" && description.isNotEmpty() && code.isNotEmpty() && stock.isNotEmpty()){

            showDialog("Registrando...")

            val product = Product(
                code,
                urlImageProduct,
                description,
                category,
                code,
                list,
                priceForPza,
                priceForDoc,
                priceForJgo,
                stock
            )
            mProductProvider.create(product, this, code)
        }else{
            Toast.makeText(
                this@AddProductActivity,
                "Complete los campos!",
                Toast.LENGTH_SHORT
            ).show()
        }

    }


    fun productUploadSuccess(){
        hideDialog()
        Toast.makeText(
            this@AddProductActivity,
            "Producto subido!",
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(position : Int){
        var sum = 0
        items.removeAt(position)
        //adapter.notifyItemRemoved(position)
        adapter.notifyDataSetChanged()
        adapter.notifyItemRangeChanged(position, items.size)

        for(i in items){
            sum += i.quantity
        }
        if (sum == 0){
            binding.edtStock.setText("")
        }else{
            binding.edtStock.setText(sum.toString())
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun subtract(position : Int){
        var sum = 0
        val quantity = items[position].quantity
        val newQuantity = quantity - 1

        if (newQuantity <= 1){
            items[position].quantity = 1
        }else{
            items[position].quantity = newQuantity
        }
        for(i in items){
            sum += i.quantity
        }
        if (sum == 0){
            binding.edtStock.setText("")
        }else{
            binding.edtStock.setText(sum.toString())
        }

        adapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun add(position : Int){
        var sum = 0
        val quantity = items[position].quantity
        val newQuantity = quantity + 1

        items[position].quantity = newQuantity

        for(i in items){
            sum += i.quantity
        }
        if (sum == 0){
            binding.edtStock.setText("")
        }else{
            binding.edtStock.setText(sum.toString())
        }

        adapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
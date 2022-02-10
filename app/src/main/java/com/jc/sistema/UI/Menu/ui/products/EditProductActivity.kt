package com.jc.sistema.UI.Menu.ui.products

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.zxing.integration.android.IntentIntegrator
import com.jc.sistema.Models.Colors
import com.jc.sistema.Models.Product
import com.jc.sistema.Providers.ImageProvider
import com.jc.sistema.Providers.ProductProvider
import com.jc.sistema.R
import com.jc.sistema.Utils.BaseActivity
import com.jc.sistema.Utils.CaptureCodeBar
import com.jc.sistema.Utils.Constants
import com.jc.sistema.Utils.FileUtil
import com.jc.sistema.databinding.ActivityEditProductBinding
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_product.*
import java.io.File
import java.util.*


class EditProductActivity : BaseActivity() {

    private lateinit var binding : ActivityEditProductBinding
    private var mImageFileProfile: File? = null
    private lateinit var mImageProvider: ImageProvider
    private lateinit var mProductProvider: ProductProvider
    private var category : String  = ""
    var urlImageProduct = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = this.intent
        val bundle = intent.extras
        val editProduct : Product = bundle!!.getSerializable(Constants.KEY_ADAPTER) as Product
        val labels = resources.getStringArray(R.array.category)
        var setCategoryPosition = 0

        checkingData(editProduct)

        mImageProvider = ImageProvider("products")
        mProductProvider = ProductProvider()


        for (i in labels.indices) {
            if (labels[i] == editProduct.category){
                setCategoryPosition = i } }

        val adapterSpinnerCategory = ArrayAdapter.createFromResource(
            this,
            R.array.category,
            R.layout.support_simple_spinner_dropdown_item
        )

        binding.spCategory.adapter = adapterSpinnerCategory
        binding.spCategory.setSelection(setCategoryPosition)
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

        if (!checkColor.isChecked){
            binding.tilStock.visibility = View.VISIBLE
        }

        checkColor.setOnClickListener {
            if (checkColor.isChecked){
                binding.spColors.visibility = View.VISIBLE
                binding.linearColors.visibility = View.VISIBLE
                binding.tilStock.visibility = View.GONE

                val adapterSpinnerColors = ArrayAdapter.createFromResource(
                    this,
                    R.array.color,
                    R.layout.support_simple_spinner_dropdown_item
                )
                binding.spColors.adapter = adapterSpinnerColors
                binding.spColors.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        val color = parent!!.getItemAtPosition(position).toString()
                        val red = binding.linearRed
                        val green = binding.linearGreen
                        val white = binding.linearWhite

                        if (color == "Rojo" && !red.isVisible){
                            red.visibility = View.VISIBLE
                        }
                        else if (color == "Verde" && !green.isVisible){
                            green.visibility = View.VISIBLE
                        }
                        else if (color == "Blanco" && !white.isVisible){
                            white.visibility = View.VISIBLE
                        }

                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}}
                deleteByColor()
            }
            else{
                binding.tilStock.visibility = View.VISIBLE
                clearAllColors()
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
        binding.btnUpdateProduct.setOnClickListener {
            //register()
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

    private fun clearAllColors(){
        binding.spColors.adapter = null
        binding.spColors.visibility = View.GONE
        binding.linearColors.visibility = View.GONE

    }

    private fun clearSpinner(){
        binding.spColors.adapter = null
        val adapterSpinnerColors = ArrayAdapter.createFromResource(
            this,
            R.array.color,
            R.layout.support_simple_spinner_dropdown_item
        )
        binding.spColors.adapter = adapterSpinnerColors
        binding.spColors.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val color = parent!!.getItemAtPosition(position).toString()
                val red = binding.linearRed
                val green = binding.linearGreen
                val white = binding.linearWhite

                if (color == "Rojo" && !red.isVisible){
                    red.visibility = View.VISIBLE
                }
                else if (color == "Verde" && !green.isVisible){
                    green.visibility = View.VISIBLE
                }
                else if (color == "Blanco" && !white.isVisible){
                    white.visibility = View.VISIBLE
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}}
    }

    private fun deleteByColor(){
        binding.ivDeleteColorRed.setOnClickListener {
            binding.linearRed.visibility = View.GONE
            binding.edtRedQuantity.setText("")
            clearSpinner()
        }
        binding.ivDeleteColorGreen.setOnClickListener {
            binding.linearGreen.visibility = View.GONE
            binding.edtGreenQuantity.setText("")
            clearSpinner()
        }
        binding.ivDeleteColorWhite.setOnClickListener {
            binding.linearWhite.visibility = View.GONE
            binding.edtWhiteQuantity.setText("")
            clearSpinner()
        }
    }

    /*
    private fun register() {
        val description = binding.edtDescription.text.toString()
        val code = binding.edtCodeProduct.text.toString()
        val priceForPza = binding.edtPriceForPza.text.toString()
        val priceForDoc = binding.edtPriceForDoc.text.toString()
        val priceForJgo = binding.edtPriceForJgo.text.toString()

        val red : Int = if (binding.edtRedQuantity.text.toString() == ""){
            0
        }else{
            binding.edtRedQuantity.text.toString().toInt()
        }
        val green : Int = if (binding.edtGreenQuantity.text.toString() == ""){
            0
        }else{
            binding.edtGreenQuantity.text.toString().toInt()
        }
        val white : Int = if (binding.edtWhiteQuantity.text.toString() == ""){
            0
        }else{
            binding.edtWhiteQuantity.text.toString().toInt()
        }


        val sum   : Int = red+green+white
        var stock = binding.edtStock.text.toString()

        if (binding.chkColor.isChecked){
            stock = sum.toString()
        }

        //val color = Colors(red, green, white)

        if (urlImageProduct != "" && description.isNotEmpty() && code.isNotEmpty() && stock.isNotEmpty()){

            showDialog("Registrando...")

            val product = Product(
                code,
                urlImageProduct,
                description,
                category,
                code,
                color,
                priceForPza,
                priceForDoc,
                priceForJgo,
                stock
            )
            //mProductProvider.create(product, this, code)
        }else{
            Toast.makeText(
                this,
                "Complete los campos!",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
*/

    private fun checkingData(editProduct: Product){

        val path: String = getExternalFilesDir(null).toString()+"/"+editProduct.id+".jpg"
        val file = File(path)
        val imageUri: Uri = Uri.fromFile(file)

        if (file.exists()){
            Picasso.with(this).load(imageUri).into(binding.ivProductImage)
        }else{
            Picasso.with(this).load(editProduct.image).into(binding.ivProductImage)
        }

        binding.edtCodeProduct.setText(editProduct.code)
        binding.edtDescription.setText(editProduct.description)
        deleteByColor()
        clearSpinner()


        if (editProduct.price_for_pza != ""){
            binding.chkPricePza.isChecked = true
            binding.tilPriceForPza.visibility = View.VISIBLE
            binding.edtPriceForPza.setText(editProduct.price_for_pza)
        }
        if (editProduct.price_for_doc != ""){
            binding.chkPriceDoc.isChecked = true
            binding.tilPriceForDoc.visibility = View.VISIBLE
            binding.edtPriceForDoc.setText(editProduct.price_for_doc)
        }
        if (editProduct.price_for_jgo != ""){
            binding.chkPriceJgo.isChecked = true
            binding.tilPriceForJgo.visibility = View.VISIBLE
            binding.edtPriceForJgo.setText(editProduct.price_for_jgo)
        }
    }

  /*  fun productUploadSuccess(){
        hideDialog()
        Toast.makeText(
            this@AddProductActivity,
            "Producto subido!",
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }*/

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
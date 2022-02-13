package com.jc.sistema.UI.Menu.ui.products

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.zxing.integration.android.IntentIntegrator
import com.jc.sistema.Adapters.ColorAdapter
import com.jc.sistema.Models.Colors
import com.jc.sistema.Models.Product
import com.jc.sistema.Providers.ImageProvider
import com.jc.sistema.Providers.ProductProvider
import com.jc.sistema.R
import com.jc.sistema.Utils.*
import com.jc.sistema.databinding.ActivityAddProductBinding
import kotlinx.android.synthetic.main.activity_add_product.*
import top.defaults.colorpicker.ColorPickerPopup
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class AddProductActivity : BaseActivity() {

    private lateinit var binding : ActivityAddProductBinding
    private lateinit var mProductProvider: ProductProvider
    private var category : String  = ""
    private var adapter =  ColorAdapter(this, emptyList())
    private var items = ArrayList<Colors>()
    private var valueColor : Int = 0
    private var mSelectedImageFileUri: Uri? = null
    private var mImageURL : String = ""


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
            setupPermission()
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
                var c = 0
                if (items.size >= 0){
                    for (i in 0 until items.size){
                        if (items[i].value == valueColor){
                            c += 1
                        }
                    }
                    if (c >= 1){
                        Toast.makeText(this,"Ya existe en la lista este color!",Toast.LENGTH_SHORT).show()
                    }else{
                        items.add(data)
                        adapter.notifyDataSetChanged()
                        for(j in items){
                            sum += j.quantity
                        }
                        binding.edtStock.setText(sum.toString())
                    }
                }

            }else{
                Toast.makeText(this,"Ingrese la cantidad",Toast.LENGTH_SHORT).show()
            }

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

    private fun register(list : ArrayList<Colors>) {
        val description = binding.edtDescription.text.toString()
        val code = binding.edtCodeProduct.text.toString()
        val priceForPza = binding.edtPriceForPza.text.toString()
        val priceForDoc = binding.edtPriceForDoc.text.toString()
        val priceForJgo = binding.edtPriceForJgo.text.toString()
        val stock = binding.edtStock.text.toString()

        if (mImageURL != "" && description.isNotEmpty() && code.isNotEmpty() && stock.isNotEmpty()){

            showDialog("Registrando...")

            val product = Product(
                code,
                mImageURL,
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

    private fun setupPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            Constants.showImageChooser(this)
        } else {
            /*Requests permissions to be granted to this application. These permissions
             must be requested in your manifest, they should not be granted to your app,
             and they should have protection level*/
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_STORAGE_PERMISSION_CODE
            )
        }
    }

    /**
     * This function will identify the result of runtime permission after the user allows or deny permission based on the unique code.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    "Permiso de lectura denegado!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null) {
            mSelectedImageFileUri = data.data!!
            try {
                uploadProductImage()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (scanResult != null) {
            if (scanResult.contents == null) {
                //Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show()
            } else {
                binding.edtCodeProduct.setText(scanResult.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun uploadProductImage() {
        showDialog("Subiendo imagen...")
        ImageProvider().uploadImageToCloudStorage(this,mSelectedImageFileUri,Constants.IMAGE_PROFILE)
    }

    fun imageUploadSuccess(imageURL: String){
        GlideLoader(this).loadPicture(mSelectedImageFileUri!!, binding.ivProductImage)
        hideDialog()
        mImageURL = imageURL
        Toast.makeText(this, "Subido!", Toast.LENGTH_LONG).show()
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
package com.jc.sistema.UI.Menu.ui.products

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
import com.jc.sistema.databinding.ActivityEditProductBinding
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_product.*
import top.defaults.colorpicker.ColorPickerPopup
import java.io.File
import java.io.IOException
import java.util.*


class EditProductActivity : BaseActivity() {

    private lateinit var binding : ActivityEditProductBinding
    private var mImageFileProfile: File? = null
    private lateinit var mProductProvider: ProductProvider
    private var category : String  = ""
    var urlImageProduct = ""
    private var adapter =  ColorAdapter(this, emptyList())
    private var items = ArrayList<Colors>()
    private var valueColor : Int = 0
    private var mSelectedImageFileUri: Uri? = null
    private var mImageURL : String = ""

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = this.intent
        val bundle = intent.extras
        val data : Product = bundle!!.getSerializable(Constants.KEY_ADAPTER) as Product
        val labels = resources.getStringArray(R.array.category)
        var setCategoryPosition = 0
        val checkColor = binding.chkColor

        items = ArrayList<Colors>()
        valueColor = 0

        binding.rvColors.layoutManager = LinearLayoutManager(this)
        binding.rvColors.setHasFixedSize(true)
        adapter = ColorAdapter(this, items)
        binding.rvColors.adapter = adapter

        mProductProvider = ProductProvider()

        for (i in labels.indices) {
            if (labels[i] == data.category){
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

        fetchData(data)

        if (!checkColor.isChecked) {
            binding.tilStock.visibility = View.VISIBLE

        }

        checkColor.setOnClickListener {

            if (checkColor.isChecked) {

                binding.linearColors.visibility = View.VISIBLE
                binding.lnRvColors.visibility = View.VISIBLE
                binding.btnPickColor.setBackgroundColor(0)
                binding.tilStock.isEnabled = false
                binding.edtStock.setText(data.stock)

            } else {
                binding.tilStock.isEnabled = true
                valueColor = 0
                binding.edtStockColor.setText("")
                binding.edtStock.setText(data.stock)
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
        binding.btnUpdateProduct.setOnClickListener {
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

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchData(data: Product) {

        mImageURL = data.image
        val path: String = getExternalFilesDir(null).toString()+"/"+data.id+".jpg"
        val file = File(path)
        val imageUri: Uri = Uri.fromFile(file)

        if (file.exists()){
            GlideLoader(this).loadPicture(imageUri, binding.ivProductImage)
        }else{
            GlideLoader(this).loadPicture(data.image, binding.ivProductImage)
        }

        binding.edtDescription.setText(data.description)
        binding.edtCodeProduct.setText(data.code)

        if (data.arrayColors.isNotEmpty()){
            binding.chkColor.isChecked = true
            binding.linearColors.visibility = View.VISIBLE
            binding.tilStock.isEnabled = false
            binding.lnRvColors.visibility = View.VISIBLE
            binding.btnPickColor.setBackgroundColor(0)
            adapter = ColorAdapter(this, data.arrayColors)
            binding.rvColors.adapter = adapter
            adapter.notifyDataSetChanged()
            items = data.arrayColors
        }

        binding.edtStock.setText(data.stock)

        if (data.price_for_pza != ""){
            binding.chkPricePza.isChecked = true
            binding.tilPriceForPza.visibility = View.VISIBLE
            binding.edtPriceForPza.setText(data.price_for_pza)
        }
        if (data.price_for_doc != ""){
            binding.chkPriceDoc.isChecked = true
            binding.tilPriceForDoc.visibility = View.VISIBLE
            binding.edtPriceForDoc.setText(data.price_for_doc)
        }
        if (data.price_for_jgo != ""){
            binding.chkPriceJgo.isChecked = true
            binding.tilPriceForJgo.visibility = View.VISIBLE
            binding.edtPriceForJgo.setText(data.price_for_jgo)
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

        if (!binding.chkColor.isChecked){
            list.clear()
        }

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
                this,
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
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {

            mSelectedImageFileUri = data.data!!

            try {
                uploadProductImage()
            } catch (e: IOException) {
                e.printStackTrace()
            }
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
            this,
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
package com.jc.sistema.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jc.sistema.Models.Product
import com.jc.sistema.UI.Menu.ui.products.EditProductActivity
import com.jc.sistema.Utils.Constants
import com.jc.sistema.databinding.ItemProductLayoutBinding
import com.squareup.picasso.Picasso
import java.io.File


open class ProductAdapter(
    private val context: Context,
    private var list: ArrayList<Product>
) : RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: ItemProductLayoutBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemProductLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        val path: String = context.getExternalFilesDir(null).toString()+"/"+model.id+".jpg"
        val file = File(path)
        val imageUri: Uri = Uri.fromFile(file)

        if (file.exists()){
            Picasso.with(context).load(imageUri).into(holder.binding.ivItemImage)
        }else{
            Picasso.with(context).load(model.image).into(holder.binding.ivItemImage)
        }

        holder.binding.tvItemDescription.text = model.description
        holder.binding.tvCategory.text = model.category

        if (model.price_for_pza.isNotEmpty()){
            holder.binding.lnPricePza.visibility = View.VISIBLE
            holder.binding.tvItemPriceForPza.text = "S/"+model.price_for_pza
        }
        if (model.price_for_doc.isNotEmpty()){
            holder.binding.lnPriceDoc.visibility = View.VISIBLE
            holder.binding.tvItemPriceForDoc.text = "S/"+model.price_for_doc
        }
        if (model.price_for_jgo.isNotEmpty()){
            holder.binding.lnPriceJgo.visibility = View.VISIBLE
            holder.binding.tvItemPriceForJgo.text = "S/"+model.price_for_jgo
        }

        holder.binding.tvStock.text = model.stock
        holder.binding.linearItem.setOnClickListener {
            val intent = Intent(it.context, EditProductActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(Constants.KEY_ADAPTER, model)
            intent.putExtras(bundle)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
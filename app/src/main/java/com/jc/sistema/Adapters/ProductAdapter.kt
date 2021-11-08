package com.jc.sistema.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.jc.sistema.Models.Colors
import com.jc.sistema.Models.Product
import com.jc.sistema.databinding.ItemLayoutBinding
import com.squareup.picasso.Picasso

open class ProductAdapter (
    private val context: Context,
    private var list: ArrayList<Product>
) : RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        if (model.image.isNotEmpty()){
            Picasso.with(context).load(model.image).into(holder.binding.ivItemImage)
        }
        holder.binding.tvItemName.text = model.description
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

        if(model.colors.blanco == 0 && model.colors.rojo == 0 && model.colors.verde == 0)
        {
            holder.binding.lnColors.visibility = View.GONE
        }
        else{
            holder.binding.lnColors.visibility = View.VISIBLE
            if (model.colors.rojo != 0){holder.binding.clRed.visibility = View.VISIBLE}
            if (model.colors.verde != 0){holder.binding.clGreen.visibility = View.VISIBLE}
            if (model.colors.blanco != 0){holder.binding.clWhite.visibility = View.VISIBLE}
        }

        holder.binding.tvStock.text = model.stock
        holder.binding.linearItem.setOnClickListener {
            Toast.makeText(context,model.id, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
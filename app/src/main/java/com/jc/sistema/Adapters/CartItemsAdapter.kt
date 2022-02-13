package com.jc.sistema.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jc.sistema.Models.Cart
import com.jc.sistema.databinding.CartItemsListBinding
import com.squareup.picasso.Picasso
import java.io.File

open class CartItemsAdapter (
    private val context: Context,
    private var list: ArrayList<Cart>
) : RecyclerView.Adapter<CartItemsAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: CartItemsListBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemsAdapter.MyViewHolder {
        val binding = CartItemsListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartItemsAdapter.MyViewHolder, position: Int) {
        val model = list[position]
        val path: String = context.getExternalFilesDir(null).toString()+"/"+model.product_id+".jpg"
        val file = File(path)
        val imageUri: Uri = Uri.fromFile(file)

        if (file.exists()){
            Picasso.with(context).load(imageUri).into(holder.binding.ivItemImage)
        }else{
            Picasso.with(context).load(model.image).into(holder.binding.ivItemImage)
        }

        holder.binding.tvItemDescription.text = model.description
        holder.binding.tvQuantity.text = model.quantity
        holder.binding.tvValue.text = model.valueSelected
        holder.binding.priceValue.text = model.valueSelected+" S/"
        holder.binding.tvPriceValue.text = model.price
        holder.binding.tvTotal.text = model.total_price

    }

    override fun getItemCount(): Int {
        return list.size
    }
}
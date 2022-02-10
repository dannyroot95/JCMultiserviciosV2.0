package com.jc.sistema.Adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jc.sistema.Models.Colors
import com.jc.sistema.UI.Menu.ui.products.AddProductActivity
import com.jc.sistema.databinding.ItemsColorsBinding

class ColorAdapter(private val activity: Activity, private var list: List<Colors>):  RecyclerView.Adapter<ColorAdapter.MyViewHolder>()  {

    inner class MyViewHolder(val binding: ItemsColorsBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemsColorsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        holder.binding.itemColor.setBackgroundColor(model.value)
        holder.binding.itemQunatityColor.text = model.quantity.toString()
        holder.binding.itemBtnDelete.setOnClickListener {
            when(activity){
               is AddProductActivity -> {
                   activity.deleteItem(position)
               }
            }

        }
        holder.binding.itemSubtract.setOnClickListener {
            when(activity){
                is AddProductActivity -> {
                    activity.subtract(position)
                }
            }
        }
        holder.binding.itemAdd.setOnClickListener {
            when(activity){
                is AddProductActivity -> {
                    activity.add(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
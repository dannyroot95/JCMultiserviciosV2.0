package com.jc.sistema.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jc.sistema.Models.Order
import com.jc.sistema.databinding.ItemOrderLayoutBinding

class OrderAdapter(private val context: Context, private var list: ArrayList<Order>) : RecyclerView.Adapter<OrderAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: ItemOrderLayoutBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdapter.MyViewHolder {
        val binding = ItemOrderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderAdapter.MyViewHolder, position: Int) {
        val model = list[position]
        holder.binding.tvItemNumOrder.text = model.num_order
        holder.binding.tvClient.text = model.client_name
        holder.binding.tvDni.text = model.dni
        holder.binding.tvStatus.text = model.status

    }

    override fun getItemCount(): Int {
        return list.size
    }
}
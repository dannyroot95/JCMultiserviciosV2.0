package com.jc.sistema.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jc.sistema.Models.Users
import com.jc.sistema.Utils.Constants
import com.jc.sistema.databinding.ItemUserLayoutBinding
import com.squareup.picasso.Picasso

open class UserAdapter (private val context: Context,
private var list: ArrayList<Users>
) : RecyclerView.Adapter<UserAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: ItemUserLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.MyViewHolder {
        val binding = ItemUserLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserAdapter.MyViewHolder, position: Int) {
        val model = list[position]
        if (model.image.isNotEmpty()){
            Picasso.with(context).load(model.image).into(holder.binding.itImageUser)
        }
        holder.binding.itName.text = model.full_name
        holder.binding.itTypeUser.setTextColor(Color.parseColor("#B27A00"))
        holder.binding.itTypeUser.text = model.type_user

    }

    override fun getItemCount(): Int {
        return list.size
    }
}
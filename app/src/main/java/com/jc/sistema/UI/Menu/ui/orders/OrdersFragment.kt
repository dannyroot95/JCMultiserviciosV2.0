package com.jc.sistema.UI.Menu.ui.orders

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.jc.sistema.Adapters.OrderAdapter
import com.jc.sistema.Models.Order
import com.jc.sistema.Providers.OrderProvider
import com.jc.sistema.UI.Menu.ui.products.AddProductActivity
import com.jc.sistema.databinding.OrdersFragmentBinding

class OrdersFragment : Fragment() {

    private lateinit var binding : OrdersFragmentBinding
    private var mOrder : OrderProvider = OrderProvider()
    private lateinit var itemList: ArrayList<Order>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = OrdersFragmentBinding.inflate(inflater, container, false)
        binding.floatingCart.setOnClickListener {
            startActivity(Intent(context, CartListActivity::class.java))
        }
        return binding.root
    }

    private fun getOrderList(){
        mOrder.getAllOrderList(this)
    }

    fun successOrderList(orderList: ArrayList<Order>) {
        if (orderList.size > 0) {
            binding.rvDashboardItems.visibility = View.VISIBLE
            binding.tvNoDashboardItemsFound.visibility = View.GONE
            binding.lnProgress.visibility = View.GONE

            binding.rvDashboardItems.layoutManager = LinearLayoutManager(activity)
            binding.rvDashboardItems.setHasFixedSize(true)

            val adapter = OrderAdapter(requireActivity(), orderList)
            binding.rvDashboardItems.adapter = adapter
            itemList = orderList
        }else {
            binding.lnProgress.visibility = View.GONE
            binding.rvDashboardItems.visibility = View.GONE
            binding.tvNoDashboardItemsFound.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        getOrderList()
    }

}
package com.jc.sistema.UI.Menu.ui.products

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jc.sistema.Adapters.ProductAdapter
import com.jc.sistema.Models.Product
import com.jc.sistema.Providers.ProductProvider
import com.jc.sistema.R
import com.jc.sistema.databinding.FragmentProductBinding
import java.util.ArrayList

class ProductFragment : Fragment() {

    private lateinit var productViewModel: ProductViewModel
    private var mProduct : ProductProvider = ProductProvider()
    private lateinit var binding: FragmentProductBinding
    private lateinit var itemList: ArrayList<Product>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        productViewModel =
            ViewModelProvider(this).get(ProductViewModel::class.java)
        binding = FragmentProductBinding.inflate(inflater,container,false)
        val textView: TextView = binding.root.findViewById(R.id.text_home)
        productViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        val floating : FloatingActionButton = binding.root.findViewById(R.id.floating_add_product)
            floating.setOnClickListener {
                startActivity(Intent(context,AddProductActivity::class.java))
            }

        return binding.root
    }

    private fun getItemList(){
        mProduct.geProductList(this)
    }

    fun successProductList(productList: ArrayList<Product>) {
        if (productList.size > 0) {
            binding.rvDashboardItems.visibility = View.VISIBLE
            binding.tvNoDashboardItemsFound.visibility = View.GONE

            binding.rvDashboardItems.layoutManager = LinearLayoutManager(activity)
            binding.rvDashboardItems.setHasFixedSize(true)

            val adapter = ProductAdapter(requireActivity(), productList)
            binding.rvDashboardItems.adapter = adapter
            itemList = productList
        }else {
            binding.rvDashboardItems.visibility = View.GONE
            binding.tvNoDashboardItemsFound.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        getItemList()
    }

}
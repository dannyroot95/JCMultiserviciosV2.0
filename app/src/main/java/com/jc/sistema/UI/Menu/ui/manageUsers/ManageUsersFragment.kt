package com.jc.sistema.UI.Menu.ui.manageUsers

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
import com.jc.sistema.Adapters.ProductAdapter
import com.jc.sistema.Adapters.UserAdapter
import com.jc.sistema.Models.Users
import com.jc.sistema.Providers.AuthenticationProvider
import com.jc.sistema.Providers.UserProvider
import com.jc.sistema.R
import com.jc.sistema.UI.Menu.ui.products.AddProductActivity
import com.jc.sistema.databinding.FragmentProductBinding
import com.jc.sistema.databinding.FragmentUsersBinding
import kotlinx.android.synthetic.main.fragment_users.*
import java.util.ArrayList

class ManageUsersFragment : Fragment() {

    private lateinit var manageUsersViewModel: ManageUsersViewModel
    private var mUsers : UserProvider = UserProvider()
    private var mAuth : AuthenticationProvider = AuthenticationProvider()
    private lateinit var binding: FragmentUsersBinding
    private lateinit var itemList: ArrayList<Users>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        manageUsersViewModel =
            ViewModelProvider(this).get(ManageUsersViewModel::class.java)
        binding = FragmentUsersBinding.inflate(inflater,container,false)
        val textView: TextView = binding.root.findViewById(R.id.text_gallery)
        manageUsersViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        binding.floatingAddUsers.setOnClickListener {
            startActivity(Intent(context, AddUserActivity::class.java))
        }

        return binding.root
    }

    private fun getItemList(){
        mUsers.getUserList(this)
    }

    fun successUserList(userList: ArrayList<Users>) {
        if (userList.size > 0) {
            binding.rvDashboardUsers.visibility = View.VISIBLE
            binding.tvNoDashboardUsersFound.visibility = View.GONE
            binding.lnProgress.visibility = View.GONE

            binding.rvDashboardUsers.layoutManager = GridLayoutManager(activity,2)
            binding.rvDashboardUsers.setHasFixedSize(true)

            val adapter = UserAdapter(requireActivity(), userList)
            binding.rvDashboardUsers.adapter = adapter
            itemList = userList
        }else {
            binding.lnProgress.visibility = View.GONE
            binding.rvDashboardUsers.visibility = View.GONE
            binding.tvNoDashboardUsersFound.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        getItemList()
    }


}
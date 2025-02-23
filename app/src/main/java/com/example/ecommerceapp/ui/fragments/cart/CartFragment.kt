package com.example.ecommerceapp.ui.fragments.cart

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.example.ecommerceapp.R
import com.example.ecommerceapp.adapters.CartAdapter
import com.example.ecommerceapp.data.local.database.AppDatabase
import com.example.ecommerceapp.data.local.entities.ProductModel
import com.example.ecommerceapp.data.local.viewmodel.ProductViewModel
import com.example.ecommerceapp.databinding.FragmentCartBinding
import com.example.ecommerceapp.ui.activities.address.AddressActivity
import com.example.ecommerceapp.utils.Constants.Companion.INFO
import com.example.ecommerceapp.utils.Constants.Companion.IS_CART
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.lifecycleScope
import com.example.ecommerceapp.utils.Constants.Companion.TOTAL_COST
import com.example.ecommerceapp.utils.Constants.Companion.TOTAL_COST_VALUE
import com.example.ecommerceapp.utils.showToast


@AndroidEntryPoint
class CartFragment : Fragment() {
    private lateinit var list: ArrayList<String>
    private lateinit var binding: FragmentCartBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var preferenceCost: SharedPreferences
    private lateinit var preference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productViewModel = ViewModelProvider(requireActivity())[ProductViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        preferenceCost =
            requireContext().getSharedPreferences(TOTAL_COST, AppCompatActivity.MODE_PRIVATE)
        preference =
            requireContext().getSharedPreferences(INFO, AppCompatActivity.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean(IS_CART, false)
        editor.apply()

        if(preferenceCost.getInt(TOTAL_COST_VALUE,0) == 0){
            binding.noDataTextView.visibility = View.VISIBLE
            binding.noDataImageView.visibility = View.VISIBLE
        }else{
            binding.noDataTextView.visibility = View.INVISIBLE
            binding.noDataImageView.visibility = View.INVISIBLE
        }

        val dao = AppDatabase.getInstance(requireContext()).productDao()

        list = ArrayList()

        dao.getAllProduct().observe(requireActivity()) {
            binding.recyclerCart.adapter = CartAdapter(requireContext(), it, productViewModel)

            list.clear()
            for (data in it) {
                list.add(data.productId)
            }

            totalCost(it)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_cart_fragment, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.menu_delete_all) {
                    deleteAllData()
                }
                return true
            }
        },viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    private var total = 0
    private fun totalCost(data: List<ProductModel>?) {

        total = preferenceCost.getInt(TOTAL_COST_VALUE,0)

        binding.totalCostTextView.text = "$total"

        binding.checkoutButton.setOnClickListener {
            if (total > 0) {
                val intent = Intent(context, AddressActivity::class.java)
                intent.putExtra(TOTAL_COST_VALUE, total)
                intent.putExtra("productIds", list)
                startActivity(intent)
            }
            if(binding.totalCostTextView.text.toString() == "0"){
                requireContext().showToast("You haven't added anything to the cart yet")
            }
        }

    }

    private fun deleteAllData(){
        val builder= AlertDialog.Builder(requireContext())

        builder.setPositiveButton("Yes"){_,_->
            productViewModel.deleteAllData()
            total = 0
            preferenceCost.edit().putInt(TOTAL_COST_VALUE, total).apply()
            binding.totalCostTextView.text = total.toString()
            Toast.makeText(requireContext(),"Successfully Removed Everything!", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("No"){_,_->}
        builder.setTitle("Delete Everything?")
        builder.setMessage("Are you sure you want to remove everything?")
        builder.create().show()
    }

}
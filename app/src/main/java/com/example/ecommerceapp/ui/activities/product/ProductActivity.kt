package com.example.ecommerceapp.ui.activities.product

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.example.ecommerceapp.R
import com.example.ecommerceapp.adapters.ProductAdapter
import com.example.ecommerceapp.data.network.FirebaseDb
import com.example.ecommerceapp.databinding.ActivityProductBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductActivity : AppCompatActivity() ,SearchView.OnQueryTextListener{
    private lateinit var binding: ActivityProductBinding
    private lateinit var firebaseDb:FirebaseDb
    private lateinit var productAdapter: ProductAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseDb = FirebaseDb(this)
        setSupportActionBar(binding.toolbarProductProduct)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarProductProduct.navigationIcon?.setColorFilter(
            ContextCompat.getColor(this, R.color.white),
            PorterDuff.Mode.SRC_ATOP
        )

        productAdapter = ProductAdapter(this, ArrayList())

        loadProducts()

        setUpMenu()

    }

    private fun setUpMenu() {
        val menuHost: MenuHost = this
        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_product_activity, menu)

                val search = menu.findItem(R.id.menu_search_product)
                val searchView = search.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@ProductActivity)

                val searchIcon = searchView?.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
                searchIcon?.setColorFilter(ContextCompat.getColor(this@ProductActivity, R.color.white), PorterDuff.Mode.SRC_IN)
                binding.toolbarProductProduct.overflowIcon?.setColorFilter(ContextCompat.getColor(this@ProductActivity, R.color.white), PorterDuff.Mode.SRC_IN)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.menu_price_low -> {
                        firebaseDb.sortByLowPrice { sortedList ->
                            val sortedArrayList = ArrayList(sortedList)
                            binding.recyclerProductActivity.adapter = ProductAdapter(this@ProductActivity, sortedArrayList)
                        }
                    }

                    R.id.menu_price_high -> {
                        firebaseDb.sortByHighPrice { sortedList ->
                            val sortedArrayList = ArrayList(sortedList)
                            binding.recyclerProductActivity.adapter = ProductAdapter(this@ProductActivity, sortedArrayList)
                        }
                    }
                }
                return true
            }

        }, this, Lifecycle.State.RESUMED)
    }

    private fun loadProducts(){
        firebaseDb.loadProducts { productModel ->
            val productModelList = ArrayList(productModel)
            binding.recyclerProductActivity.adapter = ProductAdapter(this,productModelList)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchData(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        if (newText != null) {
            searchData(newText)
        }
        return true
    }

    private fun searchData(searchQuery: String){
        firebaseDb.searchData(searchQuery){productModel ->
            val productModelList = ArrayList(productModel)
            binding.recyclerProductActivity.adapter = ProductAdapter(this, productModelList)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
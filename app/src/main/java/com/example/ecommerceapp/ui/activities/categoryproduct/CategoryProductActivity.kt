package com.example.ecommerceapp.ui.activities.categoryproduct

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
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
import com.example.ecommerceapp.adapters.CategoryProductAdapter
import com.example.ecommerceapp.adapters.ProductAdapter
import com.example.ecommerceapp.data.network.FirebaseDb
import com.example.ecommerceapp.databinding.ActivityCategoryProductBinding
import com.example.ecommerceapp.utils.Constants.Companion.CATEGORY_PRODUCT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryProductActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private var currentCategory: String = ""
    private lateinit var firebaseDb: FirebaseDb
    private lateinit var binding: ActivityCategoryProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        firebaseDb = FirebaseDb(this)

        val categoryName = intent.getStringExtra(CATEGORY_PRODUCT)
        getCategoryProducts (categoryName!!)
        supportActionBar?.title = categoryName

        setupMenu()
    }

    private fun setupMenu() {
        val menuHost: MenuHost = this
        addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_category_product, menu)

                val search = menu.findItem(R.id.menu_search_category_product)
                val searchView = search.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@CategoryProductActivity)

                val searchIcon = searchView?.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
                searchIcon?.setColorFilter(ContextCompat.getColor(this@CategoryProductActivity, R.color.white), PorterDuff.Mode.SRC_IN)
                binding.toolbarCategoryProduct.overflowIcon?.setColorFilter(ContextCompat.getColor(this@CategoryProductActivity, R.color.white), PorterDuff.Mode.SRC_IN)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.menu_price_low_category_product -> {
                        firebaseDb.sortByLowPriceInCategoryActivity(currentCategory) { sortedList ->
                            Log.e("sortedList",sortedList.toString())
                            val sortedArrayList = ArrayList(sortedList)
                            binding.recyclerCategory.adapter = ProductAdapter(this@CategoryProductActivity, sortedArrayList)
                        }
                    }

                    R.id.menu_price_high_category_product -> {
                        firebaseDb.sortByHighPriceInCategoryActivity(currentCategory) { sortedList ->
                            val sortedArrayList = ArrayList(sortedList)
                            binding.recyclerCategory.adapter = ProductAdapter(this@CategoryProductActivity, sortedArrayList)
                        }
                    }
                }
                return true
            }

        }, this, Lifecycle.State.RESUMED)

    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarCategoryProduct)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navigationIcon = binding.toolbarCategoryProduct.navigationIcon
        navigationIcon?.setColorFilter(
            ContextCompat.getColor(this, R.color.white),
            PorterDuff.Mode.SRC_ATOP
        )
    }

    private fun getCategoryProducts(category: String) {
        currentCategory = category
        firebaseDb.getCategoryProducts(category) { productList ->
            val productArrayList = ArrayList(productList)
            binding.recyclerCategory.adapter = ProductAdapter(this, productArrayList)
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        if (query != null) {
            searchCategoryProducts(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        if (newText != null) {
            searchCategoryProducts(newText)
        }
        return true
    }

    private fun searchCategoryProducts(searchQuery: String) {
        firebaseDb.searchCategoryProducts(searchQuery,currentCategory) { productList ->
            val productArrayList = ArrayList(productList)
            binding.recyclerCategory.adapter = ProductAdapter(this, productArrayList)
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
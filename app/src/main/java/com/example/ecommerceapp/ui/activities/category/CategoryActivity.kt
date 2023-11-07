package com.example.ecommerceapp.ui.activities.category

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
import com.example.ecommerceapp.adapters.CategoryFragmentAdapter
import com.example.ecommerceapp.data.network.FirebaseDb
import com.example.ecommerceapp.databinding.ActivityCategoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var firebaseDb: FirebaseDb
    private lateinit var binding: ActivityCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarCategoryActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        firebaseDb = FirebaseDb(this)

        binding.toolbarCategoryActivity.navigationIcon?.setColorFilter(
            ContextCompat.getColor(this, R.color.white),
            PorterDuff.Mode.SRC_ATOP
        )
        getCategory()
        setupMenu()

    }

    private fun setupMenu() {
        val menuHost: MenuHost = this
        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_search, menu)

                val search = menu.findItem(R.id.menu_search)
                val searchView = search.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@CategoryActivity)

                val searchIcon = searchView?.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
                searchIcon?.setColorFilter(ContextCompat.getColor(this@CategoryActivity, R.color.white), PorterDuff.Mode.SRC_IN)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }

        }, this, Lifecycle.State.RESUMED)
    }

    private fun getCategory() {
        firebaseDb.getCategory { categoryList ->
            val categoryArrayList = ArrayList(categoryList)
            binding.recyclerCategoryActivity.adapter = CategoryFragmentAdapter(this, categoryArrayList)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchCategory(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        if (newText != null) {
            searchCategory(newText)
        }
        return true
    }

    private fun searchCategory(searchQuery: String) {
        firebaseDb.searchCategory(searchQuery) { categoryList ->
            val categoryArrayList = ArrayList(categoryList)
            binding.recyclerCategoryActivity.adapter = CategoryFragmentAdapter(this, categoryArrayList)
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
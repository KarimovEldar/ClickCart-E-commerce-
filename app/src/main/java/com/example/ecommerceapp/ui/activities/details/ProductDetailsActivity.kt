package com.example.ecommerceapp.ui.activities.details

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.ecommerceapp.adapters.ColorAdapter
import com.example.ecommerceapp.data.local.database.AppDatabase
import com.example.ecommerceapp.data.local.entities.ProductModel
import com.example.ecommerceapp.data.local.viewmodel.ProductViewModel
import com.example.ecommerceapp.data.network.FirebaseDb
import com.example.ecommerceapp.databinding.ActivityProductDetailsBinding
import com.example.ecommerceapp.ui.activities.main.MainActivity
import com.example.ecommerceapp.utils.Constants.Companion.INFO
import com.example.ecommerceapp.utils.Constants.Companion.IS_CART
import com.example.ecommerceapp.utils.Constants.Companion.PRODUCT_ID
import com.example.ecommerceapp.utils.Constants.Companion.TOTAL_COST
import com.example.ecommerceapp.utils.Constants.Companion.TOTAL_COST_VALUE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var firebaseDb: FirebaseDb
    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var productViewModel: ProductViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        firebaseDb = FirebaseDb(this@ProductDetailsActivity)

        loadProductDetails(intent.getStringExtra(PRODUCT_ID))

    }

    private fun loadProductDetails(productId: String?) {
        firebaseDb.loadProductDetails(productId!!){productModel ->
            val list = productModel!!.productImages
            val productName = productModel!!.productName
            val productPrice = productModel!!.productPrice
            val productDescription = productModel!!.productDescription
            val productCoverImage = productModel!!.productCoverImage
            val productColor = productModel!!.productColor

            binding.productNameProductDetailsTextView.text = productName
            binding.productPriceProductDetailsTextView.text = "$productPrice Azn"
            binding.productDescriptionProductDetailsTextView.text = productDescription
            binding.recyclerViewColor.adapter = ColorAdapter(productColor)

            val slideList = ArrayList<SlideModel>()
            for (data in list) {
                slideList.add(SlideModel(data, ScaleTypes.FIT))
            }

            setUpCartAction(productId!!, productName, productPrice, productCoverImage)
            binding.imageSlider.setImageList(slideList)

        }

    }

    private fun setUpCartAction(
        productId: String,
        name: String?,
        productPrice: String?,
        coverImg: String?
    ) {
        val productDao = AppDatabase.getInstance(this).productDao()

        if(productViewModel.isProductInCart(productId)){
            binding.addToCartTextView.text = "Go to Cart"
        }else{
            binding.addToCartTextView.text = "Add to Cart"
        }

        binding.addToCartTextView.setOnClickListener {
            if (productViewModel.isProductInCart(productId)) {
                openCard()
            } else {
                addToCard(productId,name,productPrice,coverImg)
            }
        }

    }

    private fun addToCard(
        productId: String,
        name: String?,
        productPrice: String?,
        coverImg: String?
    ) {
        val product = ProductModel(productId,name,coverImg,productPrice)
        lifecycleScope.launch(Dispatchers.IO){
            productViewModel.insertProduct(product)
            val costPreference = getSharedPreferences(TOTAL_COST, MODE_PRIVATE)
            val totalCost = costPreference.getInt(TOTAL_COST_VALUE, 0)
            val newTotalCost = totalCost + (productPrice?.toIntOrNull() ?: 0)
            costPreference.edit().putInt(TOTAL_COST_VALUE, newTotalCost).apply()
            binding.addToCartTextView.text = "Go to Cart"
        }

    }

    private fun openCard() {
        val preference = this.getSharedPreferences(INFO, MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean(IS_CART,true)
        editor.apply()

        startActivity(Intent(this@ProductDetailsActivity, MainActivity::class.java))
        finish()
    }

}
package com.example.ecommerceapp.data.local.repository

import com.example.ecommerceapp.data.local.dao.ProductDao
import com.example.ecommerceapp.data.local.entities.ProductModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class ProductRepository @Inject constructor(
    private val productDao: ProductDao
    ) {

    suspend fun insertProduct(product: ProductModel){
        productDao.insertProduct(product)
    }

    suspend fun deleteAllData() {
        productDao.deleteAllProduct()
    }

    suspend fun deleteData(product: ProductModel){
        productDao.deleteProduct(product)
    }

    fun isProductInCart(productId: String): Boolean {
        return productDao.isExit(productId) != null
    }

}
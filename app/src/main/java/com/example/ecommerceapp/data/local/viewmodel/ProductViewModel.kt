package com.example.ecommerceapp.data.local.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.local.database.AppDatabase
import com.example.ecommerceapp.data.local.entities.ProductModel
import com.example.ecommerceapp.data.local.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {

    private val product = AppDatabase.getInstance(application).productDao()
    private val repository = ProductRepository(product)

    fun insertProduct(product: ProductModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertProduct(product)
        }
    }

    fun deleteAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllData()
        }
    }

    fun deleteData(product: ProductModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteData(product)
        }
    }

    fun isProductInCart(id: String) = repository.isProductInCart(id)

}
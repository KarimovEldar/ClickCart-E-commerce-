package com.example.ecommerceapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.ecommerceapp.data.local.entities.ProductModel

@Dao
interface ProductDao {

    @Insert
    suspend fun insertProduct(product: ProductModel)

    @Delete
    suspend fun deleteProduct(product: ProductModel)

    @Query("SELECT * FROM product_table")
    fun getAllProduct(): LiveData<List<ProductModel>>

    @Query("SELECT * FROM product_table WHERE productId = :id")
    fun isExit(id:String): ProductModel

    @Query("DELETE FROM product_table")
    suspend fun deleteAllProduct()

}
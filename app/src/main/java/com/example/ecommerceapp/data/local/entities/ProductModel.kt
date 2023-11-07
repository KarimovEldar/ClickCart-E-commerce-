package com.example.ecommerceapp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ecommerceapp.utils.Constants.Companion.PRODUCT_PRICE
import com.example.ecommerceapp.utils.Constants.Companion.TABLE_NAME
import javax.annotation.Nonnull

@Entity(tableName = TABLE_NAME)
data class ProductModel(
    @PrimaryKey
    @Nonnull
    val productId: String,
    @ColumnInfo(name = "productName")
    val productName: String? = "",
    @ColumnInfo(name = "productImage")
    val productImage: String? = "",
    @ColumnInfo(name = PRODUCT_PRICE)
    val productPrice: String? = "",
)
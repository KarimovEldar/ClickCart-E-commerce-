package com.example.ecommerceapp.model

data class AddProductModel(
    val productName: String? = "",
    val productDescription: String? = "",
    val productCoverImage: String? = "",
    val productCategory: String? = "",
    val productId: String? = "",
    val productPrice: String? = "",
    val productColor: ArrayList<String> = ArrayList(),
    val productLink: String? = "",
    val productImages: ArrayList<String> = ArrayList()
)
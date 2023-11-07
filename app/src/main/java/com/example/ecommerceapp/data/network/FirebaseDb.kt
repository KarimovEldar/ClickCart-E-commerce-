package com.example.ecommerceapp.data.network

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import com.example.ecommerceapp.model.AddProductModel
import com.example.ecommerceapp.model.CategoryModel
import com.example.ecommerceapp.model.ReportModel
import com.example.ecommerceapp.model.UserModel
import com.example.ecommerceapp.utils.Constants.Companion.CATEGORIES
import com.example.ecommerceapp.utils.Constants.Companion.PRODUCTS
import com.example.ecommerceapp.utils.Constants.Companion.PRODUCT_CATEGORY
import com.example.ecommerceapp.utils.Constants.Companion.REPORTS
import com.example.ecommerceapp.utils.Constants.Companion.USERS
import com.example.ecommerceapp.utils.Constants.Companion.USER_PHONE_NUMBER_KEY
import com.example.ecommerceapp.utils.Constants.Companion.USER_PREFERENCES_NAME
import com.example.ecommerceapp.utils.Constants.Companion.PRODUCT_PRICE
import com.example.ecommerceapp.utils.Constants.Companion.SLIDERS
import com.example.ecommerceapp.utils.Constants.Companion.SLIDER_IMAGES
import com.example.ecommerceapp.utils.Constants.Companion.USER_NAME
import com.example.ecommerceapp.utils.MyExtensionFunction.Companion.asCapitalized
import com.example.ecommerceapp.utils.showToast
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseDb(val context: Context) {

    private var preferenceUser: SharedPreferences =
        context.getSharedPreferences(USER_PREFERENCES_NAME, MODE_PRIVATE)

    private val usersCollectionRef = Firebase.firestore.collection(USERS)
    private var categoriesCollectionRef = Firebase.firestore.collection(CATEGORIES)
    private var productsCollectionRef = Firebase.firestore.collection(PRODUCTS)
    private val reportsCollectionRef = Firebase.firestore.collection(REPORTS)

    fun updateUser(map: HashMap<String, Any>, intent: Intent? = null) {
        usersCollectionRef
            .document(preferenceUser.getString(USER_PHONE_NUMBER_KEY, "")!!)
            .update(map).addOnSuccessListener {
                if (intent != null) {
                    context.startActivity(intent)
                }
            }
            .addOnFailureListener { e ->
                context.showToast("Error: ${e.message}")
            }
    }

    fun getCategory(callback: (List<CategoryModel>) -> Unit) {
        val categoryList = ArrayList<CategoryModel>()
        categoriesCollectionRef.get().addOnSuccessListener { querySnapshot ->
            categoryList.clear()
            for (document in querySnapshot.documents) {
                val data = document.toObject(CategoryModel::class.java)
                data?.let { categoryList.add(data) }
            }
            callback(categoryList)
        }
    }

    fun searchCategory(searchQuery: String, callback: (List<CategoryModel>) -> Unit) {
        val categoryList = mutableListOf<CategoryModel>()
        val lowercaseQuery = searchQuery.asCapitalized

        categoriesCollectionRef.get().addOnSuccessListener { querySnapshot ->
            categoryList.clear()
            for (document in querySnapshot.documents) {
                val data = document.toObject(CategoryModel::class.java)
                data?.category?.asCapitalized?.let { category ->
                    if (category.contains(lowercaseQuery)) {
                        data.let { categoryList.add(data) }
                    }
                }
            }
            callback(categoryList)
        }.addOnFailureListener { e ->
            context.showToast("Error: ${e.message}")
        }
    }

    fun getCategoryProducts(category: String?, callback: (List<AddProductModel>) -> Unit) {
        val productList = mutableListOf<AddProductModel>()
        productsCollectionRef.whereEqualTo(PRODUCT_CATEGORY, category)
            .get().addOnSuccessListener { querySnapshot ->
                productList.clear()
                for (document in querySnapshot.documents) {
                    val data = document.toObject(AddProductModel::class.java)
                    data?.let { productList.add(data) }
                }
                callback(productList)
            }.addOnFailureListener { e ->
                context.showToast("Error: ${e.message}")
            }
    }

    fun searchCategoryProducts(
        searchQuery: String,
        category: String,
        callback: (List<AddProductModel>) -> Unit
    ) {
        val productList = mutableListOf<AddProductModel>()
        val lowercaseQuery = searchQuery.asCapitalized

        productsCollectionRef.whereEqualTo(PRODUCT_CATEGORY, category)
            .get().addOnSuccessListener { querySnapshot ->
                productList.clear()
                for (document in querySnapshot.documents) {
                    val data = document.toObject(AddProductModel::class.java)
                    data?.productName?.asCapitalized?.let { productName ->
                        if (productName.contains(lowercaseQuery)) {
                            data.let { productList.add(data) }
                        }
                    }
                }
                callback(productList)
            }.addOnFailureListener { e ->
                context.showToast("Error: ${e.message}")
            }
    }

    fun sortByHighPrice(callback: (List<AddProductModel>) -> Unit) {
        sortProductsByPrice(ascending = false, callback)
    }

    fun sortByLowPrice(callback: (List<AddProductModel>) -> Unit) {
        sortProductsByPrice(ascending = true, callback)
    }

    private fun sortProductsByPrice(ascending: Boolean, callback: (List<AddProductModel>) -> Unit) {
        val list = ArrayList<AddProductModel>()

        productsCollectionRef
            .orderBy(PRODUCT_PRICE, Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener {
                list.clear()
                for (document in it.documents) {
                    val data = document.toObject(AddProductModel::class.java)
                    data?.let { list.add(data) }
                }
                list.sortBy { it.productPrice!!.toInt() }
                if (!ascending) {
                    list.reverse()
                }
                callback(list)
            }
    }

    private fun sortProductsByPriceInCategoryProduct(
        category: String,
        ascending: Boolean,
        callback: (List<AddProductModel>) -> Unit
    ) {
        val list = ArrayList<AddProductModel>()

        productsCollectionRef.whereEqualTo(PRODUCT_CATEGORY, category)
            .orderBy(PRODUCT_PRICE, Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener {
                list.clear()
                for (document in it.documents) {
                    val data = document.toObject(AddProductModel::class.java)
                    data?.let { list.add(data) }
                }
                list.sortBy { it.productPrice!!.toInt() }
                if (!ascending) {
                    list.reverse()
                }
                callback(list)
            }.addOnFailureListener { e ->
                context.showToast(e.message.toString())
                context.showToast("Error: ${e.message}")
            }
    }

    fun sortByLowPriceInCategoryActivity(
        currentCategory: String,
        callback: (List<AddProductModel>) -> Unit
    ) {
        sortProductsByPriceInCategoryProduct(
            currentCategory,
            ascending = true,
            callback
        )
    }

    fun sortByHighPriceInCategoryActivity(
        currentCategory: String,
        callback: (List<AddProductModel>) -> Unit
    ) {
        sortProductsByPriceInCategoryProduct(
            currentCategory,
            ascending = false,
            callback
        )
    }

    fun getUser(phoneNumber: String, callback: (String?) -> Unit) {
        Firebase.firestore.collection(USERS)
            .document(phoneNumber).get().addOnSuccessListener { documentSnapshot ->
                val userName = documentSnapshot.getString(USER_NAME)
                callback(userName)
            }.addOnFailureListener { e ->
                context.showToast(e.message.toString())
                callback(null)
            }
    }

    fun getSliderImages(callback: (List<String>) -> Unit) {
        Firebase.firestore.collection(SLIDERS)
            .document("mtLe4JGv1NZfruc6jGy8").get()
            .addOnSuccessListener { documentSnapshot ->
                val list = documentSnapshot.get(SLIDER_IMAGES) as? List<String> ?: emptyList()
                callback(list)
            }.addOnFailureListener { e ->
                context.showToast(e.message.toString())
                callback(emptyList())
            }
    }

    fun getProducts(callback: (List<AddProductModel>) -> Unit) {
        val list = ArrayList<AddProductModel>()
        Firebase.firestore.collection(PRODUCTS)
            .get().addOnSuccessListener { querySnapshot ->
                list.clear()
                for (document in querySnapshot.documents) {
                    val data = document.toObject(AddProductModel::class.java)
                    data?.let { list.add(data) }
                }
                callback(list)
            }.addOnFailureListener { e ->
                context.showToast(e.message.toString())
                callback(emptyList())
            }
    }

    fun loadUserInfo(phoneNumber: String, callback: (UserModel?) -> Unit) {
        usersCollectionRef
            .document(phoneNumber)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val userData = documentSnapshot.toObject(UserModel::class.java)
                callback(userData)
            }
            .addOnFailureListener { e ->
                context.showToast(e.message.toString())
                callback(null)
            }
    }

    fun loadProductDetails(productId: String, callback: (AddProductModel?) -> Unit) {
        productsCollectionRef
            .document(productId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val productData = documentSnapshot.toObject(AddProductModel::class.java)
                callback(productData)
            }
            .addOnFailureListener { e ->
                context.showToast(e.message.toString())
                callback(null)
            }
    }

    fun loadProducts(callback: (List<AddProductModel>) -> Unit) {
        productsCollectionRef
            .get()
            .addOnSuccessListener { querySnapshot ->
                val productList = mutableListOf<AddProductModel>()
                for (document in querySnapshot.documents) {
                    val data = document.toObject(AddProductModel::class.java)
                    data?.let { productList.add(data) }
                }
                callback(productList)
            }
            .addOnFailureListener { e ->
                context.showToast(e.message.toString())
                callback(emptyList())
            }
    }

    fun searchData(searchQuery: String, callback: (List<AddProductModel>) -> Unit) {
        val list = ArrayList<AddProductModel>()
        val lowercaseQuery = searchQuery.asCapitalized

        productsCollectionRef
            .get().addOnSuccessListener { querySnapshot ->
                list.clear()
                for (document in querySnapshot.documents) {
                    val data = document.toObject(AddProductModel::class.java)
                    data?.productName?.asCapitalized?.let { productName ->
                        if (productName.contains(lowercaseQuery)) {
                            data.let { list.add(data) }
                        }
                    }
                }
                callback(list)
            }
            .addOnFailureListener { e ->
                context.showToast(e.message.toString())
                callback(emptyList())
            }
    }

    fun addReport(phoneNumber: String, reportData: ReportModel, callback: (DocumentReference) -> Unit) {
        val reportRef = reportsCollectionRef.document(phoneNumber)
        reportsCollectionRef
            .document(phoneNumber)
            .set(reportData)
            .addOnSuccessListener {
                callback(reportRef)
            }
            .addOnFailureListener { e ->
                context.showToast(e.toString())
            }
    }

}
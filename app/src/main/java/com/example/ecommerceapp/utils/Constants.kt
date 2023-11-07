package com.example.ecommerceapp.utils

class Constants {

    companion object{
        //Room Database
        const val DATABASE_NAME = "product_database"
        const val TABLE_NAME = "product_table"

        //Preferences
        const val PRODUCT_ID = "id"
        const val CATEGORY_PRODUCT = "categoryProduct"
        const val INFO = "info"
        const val IS_CART = "isCart"

        //Product
        const val PRODUCTS = "products"
        const val PRODUCT_CATEGORY = "productCategory"
        const val PRODUCT_IMAGES = "productImages"
        const val PRODUCT_NAME ="productName"
        const val PRODUCT_PRICE = "productPrice"
        const val PRODUCT_DESCRIPTION = "productDescription"
        const val PRODUCT_COVER_IMAGE = "productCoverImage"
        const val PRODUCT_COLOR = "productColor"

        const val CATEGORIES = "categories"

        //User
        const val USERS = "users"
        const val USER_NAME = "userName"
        const val USER_PHONE_NUMBER = "userPhoneNumber"
        const val USER_GENDER = "gender"
        const val MALE = "Male"
        const val FEMALE = "Female"
        const val USER_COUNTRY = "country"
        const val USER_CITY = "city"
        const val USER_PIN_CODE = "pinCode"

        //Firebase
        const val SLIDERS = "sliders"
        const val SLIDER_IMAGES = "sliderImages"

        //Report
        const val REPORTS = "reports"

        // User preferences
        const val USER_PREFERENCES_NAME = "user"
        const val USER_NAME_KEY = "name"
        const val USER_PHONE_NUMBER_KEY = "number"

        //Total Cost
        const val TOTAL_COST = "Total Cost"
        const val TOTAL_COST_VALUE = "totalCostValue"

        //DataStore
        const val PREFERENCES_NAME = "clickcart_preferences"
        const val PREFERENCES_BACK_ONLINE = "backOnline"

    }

}
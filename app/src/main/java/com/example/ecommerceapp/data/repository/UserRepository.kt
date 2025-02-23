package com.example.ecommerceapp.data.repository

import android.content.Context
import com.example.ecommerceapp.model.UserModel
import com.example.ecommerceapp.utils.Constants
import com.example.ecommerceapp.utils.Constants.Companion.USERS
import com.example.ecommerceapp.utils.Constants.Companion.USER_NAME_KEY
import com.example.ecommerceapp.utils.Constants.Companion.USER_PHONE_NUMBER_KEY
import com.example.ecommerceapp.utils.Constants.Companion.USER_PREFERENCES_NAME
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

object UserRepository {

    fun storeUserData(context: Context, userName: String, phoneNumber: String) {
        // Store user data in SharedPreferences
        val preferences = context.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(USER_NAME_KEY, userName)
        editor.putString(USER_PHONE_NUMBER_KEY, phoneNumber)
        editor.apply()

        // Store user data in Firebase FireStore
        val fireStore = Firebase.firestore
        val usersCollectionRef = fireStore.collection(USERS)
        val userData = UserModel(userName, phoneNumber)
        usersCollectionRef.document(phoneNumber).set(userData)
    }
}
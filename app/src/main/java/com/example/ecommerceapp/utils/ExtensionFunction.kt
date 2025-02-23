package com.example.ecommerceapp.utils

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

class MyExtensionFunction {
    companion object {
        val String.asCapitalized
            get() = lowercase().replaceFirstChar { it.titlecase() }
    }
}
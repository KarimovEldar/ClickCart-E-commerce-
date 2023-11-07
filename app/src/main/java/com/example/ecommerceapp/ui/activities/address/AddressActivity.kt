package com.example.ecommerceapp.ui.activities.address

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ecommerceapp.R
import com.example.ecommerceapp.data.local.viewmodel.ProductViewModel
import com.example.ecommerceapp.data.network.FirebaseDb
import com.example.ecommerceapp.databinding.ActivityAddressBinding
import com.example.ecommerceapp.ui.activities.main.MainActivity
import com.example.ecommerceapp.utils.Constants
import com.example.ecommerceapp.utils.Constants.Companion.FEMALE
import com.example.ecommerceapp.utils.Constants.Companion.MALE
import com.example.ecommerceapp.utils.Constants.Companion.USER_CITY
import com.example.ecommerceapp.utils.Constants.Companion.USER_COUNTRY
import com.example.ecommerceapp.utils.Constants.Companion.USER_GENDER
import com.example.ecommerceapp.utils.Constants.Companion.USER_PHONE_NUMBER_KEY
import com.example.ecommerceapp.utils.Constants.Companion.USER_PIN_CODE
import com.example.ecommerceapp.utils.Constants.Companion.USER_PREFERENCES_NAME
import com.example.ecommerceapp.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressActivity : AppCompatActivity() {

    private var selectedGender: String = MALE
    private lateinit var binding: ActivityAddressBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var firebaseDb: FirebaseDb
    private lateinit var productViewModel: ProductViewModel
    private lateinit var preferenceCost: SharedPreferences



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = this.getSharedPreferences(USER_PREFERENCES_NAME, MODE_PRIVATE)
        firebaseDb = FirebaseDb(this@AddressActivity)
        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        preferenceCost =
            this.getSharedPreferences(Constants.TOTAL_COST, AppCompatActivity.MODE_PRIVATE)

        setUpUI()

    }

    private fun setUpUI() {
        binding.apply {
            buttonFemaleCheckBoxInActivityAddress.setOnClickListener {
                handleGenderSelection(FEMALE)
            }

            buttonMaleCheckBoxInActivityAddress.setOnClickListener {
                handleGenderSelection(MALE)
            }

            proceedToCheckoutButton.setOnClickListener {
                validateAndStoreData()
                showAlertSuccessfully()
            }
        }

        loadUserInfo()
    }

    private fun handleGenderSelection(gender: String) {
        selectedGender = gender
        binding.buttonMaleCheckBoxInActivityAddress.isChecked = (gender == MALE)
        binding.buttonFemaleCheckBoxInActivityAddress.isChecked = (gender == FEMALE)
    }

    private fun validateAndStoreData() {
        val number = binding.userPhoneNumberInActivityAddressEditText.text.toString()
        val name = binding.userNameInActivityAddressEditText.text.toString()
        val city = binding.userCityInActivityAddressEditText.text.toString()
        val pinCode = binding.userPinCodeInActivityAddressEditText.text.toString()
        val country = binding.userCountryInActivityAddressEditText.text.toString()

        if (number.isEmpty() || country.isEmpty() || name.isEmpty()) {
            showToast("Please fill all fields")
        } else {
            storeData(pinCode, city, country)
        }
    }

    private fun storeData(pinCode: String, city: String, country: String) {
        val map = hashMapOf<String, Any>(
            USER_GENDER to selectedGender,
            USER_COUNTRY to country,
            USER_CITY to city,
            USER_PIN_CODE to pinCode
        )

        firebaseDb.updateUser(map)

    }

    private fun showAlertSuccessfully() {

        productViewModel.deleteAllData()
        preferenceCost.edit().putInt(Constants.TOTAL_COST_VALUE, 0).apply()

        val bookingAlert = AlertDialog.Builder(this)
        bookingAlert.setTitle("Payment Successfully")
        bookingAlert.setMessage("Your order has been successfully received. \nYou will receive an email confirmation shortly with the details of your purchase. \nThank you for shopping with us!")
        bookingAlert.setIcon(R.drawable.success).setPositiveButton("Ok") { dialog, id ->
            startActivity(Intent(this@AddressActivity, MainActivity::class.java))
            finish()
            dialog.dismiss()
        }

        val alertDialog = bookingAlert.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }


    private fun loadUserInfo() {

        firebaseDb.loadUserInfo(preferences.getString(USER_PHONE_NUMBER_KEY, "")!!) { userData ->

            binding.apply {
                userNameInActivityAddressEditText.setText(userData!!.userName)
                userPhoneNumberInActivityAddressEditText.setText(userData!!.userPhoneNumber)
                if (userData!!.gender == MALE) {
                    buttonMaleCheckBoxInActivityAddress.isChecked = true
                } else {
                    buttonFemaleCheckBoxInActivityAddress.isChecked = true
                }
                userCountryInActivityAddressEditText.setText(userData!!.country)
                userCityInActivityAddressEditText.setText(userData!!.city)
                userPinCodeInActivityAddressEditText.setText(userData!!.pinCode)
            }

        }

    }

}

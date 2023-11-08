package com.example.ecommerceapp.ui.fragments.profile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ecommerceapp.R
import com.example.ecommerceapp.data.network.FirebaseDb
import com.example.ecommerceapp.databinding.FragmentProfileBinding
import com.example.ecommerceapp.model.ReportModel
import com.example.ecommerceapp.utils.Constants.Companion.FEMALE
import com.example.ecommerceapp.utils.Constants.Companion.MALE
import com.example.ecommerceapp.utils.Constants.Companion.USER_CITY
import com.example.ecommerceapp.utils.Constants.Companion.USER_COUNTRY
import com.example.ecommerceapp.utils.Constants.Companion.USER_GENDER
import com.example.ecommerceapp.utils.Constants.Companion.USER_NAME
import com.example.ecommerceapp.utils.Constants.Companion.USER_PHONE_NUMBER_KEY
import com.example.ecommerceapp.utils.Constants.Companion.USER_PIN_CODE
import com.example.ecommerceapp.utils.Constants.Companion.USER_PREFERENCES_NAME
import com.example.ecommerceapp.utils.showToast
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import io.ghyeok.stickyswitch.widget.StickySwitch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var preferenceUser: SharedPreferences
    private lateinit var firebaseDb: FirebaseDb
    private var userPhoneNumber: String? = ""
    private lateinit var binding: FragmentProfileBinding
    private var selectedGender: String = MALE

    private lateinit var femaleCheckBox: CheckBox
    private lateinit var maleCheckBox: CheckBox
    private lateinit var userNameEditText: EditText
    private lateinit var countryEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var pinCodeEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        firebaseDb = FirebaseDb(requireContext())
        preferenceUser =
            requireContext().getSharedPreferences(USER_PREFERENCES_NAME, AppCompatActivity.MODE_PRIVATE)
        userPhoneNumber = preferenceUser.getString(USER_PHONE_NUMBER_KEY, "")

        val auth = FirebaseAuth.getInstance()

        binding.logOutButton.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_profileFragment_to_authenticationActivity)
        }

        binding.developerLinearLayout.setOnClickListener {
            openLinkedinProfile()
        }

        binding.supportTextView.setOnClickListener {
            showProblemReportDialog(requireContext())
        }

        binding.editProfileButton.setOnClickListener {
            showEditProfileDialog(requireContext())
        }

        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)
        var nightMode = sharedPreferences.getBoolean("nightMode", false)

        if (nightMode) {
            binding.themeSwitchButton.setDirection(StickySwitch.Direction.RIGHT, false, false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        binding.themeSwitchButton.setOnClickListener{
            val editor = sharedPreferences.edit()
            if (nightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor.putBoolean("nightMode", false)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.putBoolean("nightMode", true)
            }
            editor.apply()
        }

        getUser()

        return binding.root
    }

    private fun getUser() {

        if (userPhoneNumber != null && userPhoneNumber!!.isNotEmpty()) {
            firebaseDb.getUser(userPhoneNumber!!) { userName ->
                if (userName != null) {
                    binding.profileNameTextView.text = userName
                    binding.profileNumberTextView.text = userPhoneNumber!!
                } else {
                    binding.profileNameTextView.text = R.string.user_name.toString()
                    binding.profileNumberTextView.text = "+994 xxx xx xx"
                }
            }
        }

    }

    private fun showProblemReportDialog(context: Context) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.custom_alert_dialog, null)
        val editText = view.findViewById<EditText>(R.id.editText)

        alertDialogBuilder.setView(view)
            .setTitle("Report a Problem")
            .setPositiveButton("Submit") { dialog, _ ->
                val problemDescription = editText.text.toString()

                val report = ReportModel(problemDescription)

                firebaseDb.addReport(userPhoneNumber!!,report){
                    requireContext().showToast("Feedback received. Thanks!")
                }

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    //Edit Profile
    private fun showEditProfileDialog(context: Context) {

        loadUserInfo()

        val alertDialogBuilder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.custom_edit_profile_dialog, null)

        userNameEditText = view.findViewById(R.id.userNameEditText)
        countryEditText = view.findViewById(R.id.countryEditText)
        cityEditText = view.findViewById(R.id.cityEditText)
        pinCodeEditText = view.findViewById(R.id.pinCodeEditText)
        maleCheckBox = view.findViewById(R.id.maleCheckBox)
        femaleCheckBox = view.findViewById(R.id.femaleCheckBox)

        maleCheckBox.setOnClickListener {
            handleGenderSelection(MALE)
        }

        femaleCheckBox.setOnClickListener {
            handleGenderSelection(FEMALE)
        }


        alertDialogBuilder.setView(view)
            .setTitle("Profile name")
            .setPositiveButton("Submit") { dialog, _ ->

                val userName = userNameEditText.text.toString()
                val country = countryEditText.text.toString()
                val city = cityEditText.text.toString()
                val pinCode = pinCodeEditText.text.toString()

                if(maleCheckBox.isChecked){
                    selectedGender = MALE
                }else{
                    selectedGender = FEMALE
                }

                validateData(
                    userPhoneNumber!!,
                    userName,
                    city,
                    pinCode,
                    country,
                    selectedGender
                )

                dialog.dismiss()
                recreate(requireActivity())
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun handleGenderSelection(gender: String) {
        selectedGender = gender
        maleCheckBox.isChecked = (gender == MALE)
        femaleCheckBox.isChecked = (gender == FEMALE)
    }

    private fun loadUserInfo(){

        firebaseDb.loadUserInfo(userPhoneNumber!!){ userData->

            binding.apply {
                userNameEditText.setText(userData!!.userName)
                if (userData!!.gender == MALE) {
                    maleCheckBox.isChecked = true
                } else {
                    femaleCheckBox.isChecked = true
                }
                countryEditText.setText(userData!!.country)
                cityEditText.setText(userData!!.city)
                pinCodeEditText.setText(userData!!.pinCode)
            }

        }

    }

    private fun validateData(
        number: String,
        name: String,
        city: String,
        pinCode: String,
        country: String,
        gender: String
    ) {
        if (number.isEmpty() || country.isEmpty() || name.isEmpty()) {
            requireActivity().showToast("Please fill all fields")
        } else {
            storeData(name,pinCode, city, country, gender)
        }

    }

    private fun storeData(
        name: String,
        pinCode: String,
        city: String,
        country: String,
        gender: String
    ) {
        val map = hashMapOf<String, Any>()

        map[USER_NAME] = name
        map[USER_GENDER] = gender
        map[USER_COUNTRY] = country
        map[USER_CITY] = city
        map[USER_PIN_CODE] = pinCode

        firebaseDb.updateUser(map)

    }

    private fun openLinkedinProfile() {
        val linkedinUri = Uri.parse("https://www.linkedin.com/in/karimoveldar")
        val intent = Intent(Intent.ACTION_VIEW, linkedinUri)

        intent.setPackage("com.linkedin.android")
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            val webIntent = Intent(Intent.ACTION_VIEW, linkedinUri)
            startActivity(webIntent)
        }
    }

}

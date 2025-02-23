package com.example.ecommerceapp.ui.fragments.signup

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ecommerceapp.R
import com.example.ecommerceapp.data.network.FirebaseDb
import com.example.ecommerceapp.data.repository.UserRepository
import com.example.ecommerceapp.databinding.FragmentSignUpBinding
import com.example.ecommerceapp.model.UserModel
import com.example.ecommerceapp.utils.Constants.Companion.USERS
import com.example.ecommerceapp.utils.showToast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private val usersCollectionRef = Firebase.firestore.collection(USERS)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        binding.signInTextView.setOnClickListener {
            openLogin()
        }

        binding.signUpActivityLogInButton.setOnClickListener {
            validateUser()
        }

        return binding.root
    }

    private fun validateUser() {
        if (binding.userNameInSignUpEditText.text!!.isEmpty() || binding.userNumberInSignUpEditText.text!!.isEmpty()) {
            requireContext().showToast("Please fill all fields")
        } else {
            storeData()
        }
    }

    private lateinit var builder: AlertDialog
    private fun storeData() {
        showLoadingDialog()

        val user = getUserData()

        usersCollectionRef.document(user.userPhoneNumber!!)
            .set(user).addOnSuccessListener {
                requireContext().showToast("User registered.")
                builder.dismiss()
                openLogin()

                UserRepository.storeUserData(
                    requireContext(),
                    user.userName!!,
                    user.userPhoneNumber
                )
            }.addOnFailureListener {
                builder.dismiss()
                requireContext().showToast(it.message.toString())
            }

    }

    private fun showLoadingDialog() {
        builder = AlertDialog.Builder(requireContext())
            .setTitle("Loading...")
            .setMessage("Please Wait")
            .setCancelable(false)
            .create()
        builder.show()
    }

    private fun getUserData(): UserModel {
        val countryCode = binding.countryCodeInSignUpEditText.selectedCountryCodeWithPlus
        val userName = binding.userNameInSignUpEditText.text.toString()
        val userNumber = binding.userNumberInSignUpEditText.text.toString()
        val phoneNumber = "$countryCode$userNumber"

        return UserModel(userName, phoneNumber)
    }

    private fun openLogin() {
        findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
    }

}
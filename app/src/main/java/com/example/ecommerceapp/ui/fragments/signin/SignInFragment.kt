package com.example.ecommerceapp.ui.fragments.signin

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.ecommerceapp.R
import com.example.ecommerceapp.data.repository.UserRepository
import com.example.ecommerceapp.databinding.FragmentSignInBinding
import com.example.ecommerceapp.utils.Constants
import com.example.ecommerceapp.utils.Constants.Companion.USERS
import com.example.ecommerceapp.utils.Constants.Companion.USER_NAME
import com.example.ecommerceapp.utils.Constants.Companion.USER_PHONE_NUMBER
import com.example.ecommerceapp.utils.showToast
import com.example.ecommerceapp.utils.verification.Resources
import com.example.ecommerceapp.viewmodels.AuthPhoneNumberViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@AndroidEntryPoint
class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private val viewModelPhoneAuth by viewModels<AuthPhoneNumberViewModel>()
    private val usersCollectionRef = Firebase.firestore.collection(USERS)
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loading_layout)
        dialog.setCancelable(false)

        binding.apply {

            signUpTextView.setOnClickListener {
                findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
            }

            signInActivityLogInButton.setOnClickListener {
                val countryCode = countryCodeEditText.selectedCountryCodeWithPlus
                val number = userNumberInSignInEditText.text.toString().trim()
                val phoneNumber = "$countryCode$number"

                lifecycleScope.launch {
                    Log.e("Sign in number: ", phoneNumber)
                    val userExists = checkIfUserExists(phoneNumber)
                    if (userExists) {
                        loadUserInfo(phoneNumber)
                        viewModelPhoneAuth.sendVerificationCode(phoneNumber, requireActivity())
                    } else {
                        Toast.makeText(
                            requireContext(), "User does not exist. Please sign up first",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelPhoneAuth.isVerificationInProgress.collect { resource ->
                    Log.e("Sign in resource", resource.toString())
                    when (resource) {
                        is Resources.Loading -> {
                            dialog.show()
                        }

                        is Resources.Success -> {
                            dialog.dismiss()
                            val verificationId = viewModelPhoneAuth.verificationId.value
                            Log.e("Sign in verificationId",verificationId.toString())
                            if (verificationId != null) {
                                // Proceed to VerificationOtpFragment with the verification ID.
                                val action =
                                    SignInFragmentDirections.actionSignInFragmentToOtpFragment(
                                        verificationId
                                    )
                                findNavController().navigate(action)
                            } else {
                                // Handle error: Verification ID is null.
                                Log.e("Sign in SendOtpFragment", "Verification ID is null.")
                            }
                        }

                        is Resources.Failed -> {
                            dialog.dismiss()
                            Log.d(
                                "Sign in SendOtpFragment",
                                "Verification initiation failed: ${resource.message}"
                            )
                        }

                        else -> dialog.dismiss()
                    }
                }
            }
        }
    }

    private suspend fun checkIfUserExists(phoneNumber: String): Boolean {

        val query = usersCollectionRef.whereEqualTo(USER_PHONE_NUMBER, phoneNumber)

        try {
            val querySnapshot = query.get().await()

            return !querySnapshot.isEmpty
        } catch (e: Exception) {
            requireContext().showToast(e.message.toString())
            return false
        }
    }

    private fun loadUserInfo(number: String) {
        usersCollectionRef
            .document(number)
            .get().addOnSuccessListener {
                UserRepository.storeUserData(requireContext(), it.getString(USER_NAME)!!,number)
                Log.e("Sign in User number:", number)
                Log.e("Sign in User name :",it.getString(USER_NAME)!!)
            }
            .addOnFailureListener {
                requireContext().showToast("Something went wrong")
            }
    }

 }


package com.example.ecommerceapp.viewmodels

import android.app.Activity
import com.example.ecommerceapp.utils.verification.Resources
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.utils.verification.OTPFieldsState
import com.example.ecommerceapp.utils.verification.VerificationOTPValidation
import com.example.ecommerceapp.utils.verification.validOTP
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AuthPhoneNumberViewModel @Inject constructor(private val auth: FirebaseAuth) : ViewModel() {

    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId: StateFlow<String?> = _verificationId

    private val _isVerificationInProgress =
        MutableStateFlow<Resources<Boolean>>(Resources.Unspecified())
    val isVerificationInProgress: Flow<Resources<Boolean>> = _isVerificationInProgress

    private val _validation = Channel<OTPFieldsState>()
    val validation = _validation.receiveAsFlow()

    fun sendVerificationCode(phoneNumber: String, activity: Activity) {
        runBlocking {
            _isVerificationInProgress.emit(Resources.Loading())
        }
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(e: FirebaseException) {

                Log.d("sendVerificationCode", "Verification Failed: $e")

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {

                _verificationId.value = verificationId
                _isVerificationInProgress.value =
                    Resources.Success(true, "onCodeSent: $verificationId")

                Log.d("sendVerificationCode", "onCodeSent: $verificationId")

            }

        }
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signInWithVerificationCode(verificationId: String, codeSendVerification: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, codeSendVerification)
        signInWithPhoneAuthCredential(credential, codeSendVerification)
    }

    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        codeSendVerification: String
    ) {
        if (checkValidation(codeSendVerification)) {
            runBlocking {
                _isVerificationInProgress.emit(Resources.Loading())
            }
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        Log.d(
                            "signInWithPhoneAuthCredential",
                            "signInWithCredential success: ${task.result} "
                        )
                        _isVerificationInProgress.value =
                            Resources.Success(false, "signInWithCredential success: ${task.result}")

                    } else {
                        Log.d(
                            "signInWithPhoneAuthCredential",
                            "signInWithCredential failure: ${task.exception} "
                        )
                        _isVerificationInProgress.value =
                            Resources.Failed("signInWithCredential failure: ${task.exception}")

                    }
                }
        } else {
            val otpFieldsState =
                OTPFieldsState(validOTP(codeSendVerification))
            viewModelScope.launch {
                _validation.send(otpFieldsState)
            }
        }
    }

    private fun checkValidation(
        codeSendVerification: String
    ): Boolean {
        val otpValidation = validOTP(codeSendVerification)
        return (otpValidation is VerificationOTPValidation.Success)
    }
}

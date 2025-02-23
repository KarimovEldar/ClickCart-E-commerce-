package com.example.ecommerceapp.utils.verification

fun validOTP(
    codeSendVerification: String
): VerificationOTPValidation {

    if (codeSendVerification.isEmpty()) {
        return VerificationOTPValidation.Failed("OTP is not valid!")
    }
    return VerificationOTPValidation.Success
}
package com.example.ecommerceapp.ui.fragments.otp

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.ecommerceapp.ui.activities.main.MainActivity
import com.example.ecommerceapp.R
import com.example.ecommerceapp.databinding.FragmentVerificationBinding
import com.example.ecommerceapp.utils.verification.Resources
import com.example.ecommerceapp.viewmodels.AuthPhoneNumberViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VerificationFragment : Fragment() {
    private lateinit var binding: FragmentVerificationBinding
    private val viewModelPhoneAuth by viewModels<AuthPhoneNumberViewModel>()
    private lateinit var dialog: Dialog
    private val args: VerificationFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loading_layout)
        dialog.setCancelable(false)

        setupClickListeners()
        observeVerificationStatus(view)
    }

    private fun setupClickListeners() {
        binding.userOtpButton.setOnClickListener {
            val smsCode = binding.userOtpEditText.text.toString()
            val verificationId = args.verificationId
            viewModelPhoneAuth.signInWithVerificationCode(verificationId, smsCode)
        }
    }

    private fun observeVerificationStatus(view: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelPhoneAuth.isVerificationInProgress.collect { resource ->
                    when (resource) {
                        is Resources.Loading -> handleLoadingState()
                        is Resources.Success -> handleSuccessState()
                        is Resources.Failed -> handleFailedState(resource.message.toString())
                        else -> dialog.dismiss()
                    }
                }
            }
        }
    }

    private fun handleLoadingState() {
        binding.userOtpButton.startAnimation()
    }

    private fun handleSuccessState() {
        dialog.dismiss()
        Log.d("VerificationOtpFragment", "Verification success")
        binding.userOtpButton.revertAnimation()
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun handleFailedState(errorMessage: String) {
        dialog.dismiss()
        Log.d("VerificationOtpFragment", "Verification failed: $errorMessage")
        binding.userOtpButton.revertAnimation()
    }
}
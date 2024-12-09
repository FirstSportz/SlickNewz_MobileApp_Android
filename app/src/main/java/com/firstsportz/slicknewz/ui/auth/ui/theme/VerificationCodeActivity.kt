package com.firstsportz.slicknewz.ui.auth

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.firstsportz.slicknewz.R
import com.firstsportz.slicknewz.data.model.ForgotPasswordRequest
import com.firstsportz.slicknewz.databinding.ActivityVerificationcodeBinding
import com.firstsportz.slicknewz.repository.AuthRepository
import com.firstsportz.slicknewz.ui.utils.ErrorDialog
import com.firstsportz.slicknewz.ui.utils.LoadingDialog
import com.firstsportz.slicknewz.utils.NetworkUtil
import com.firstsportz.slicknewz.viewmodel.ForgotPasswordViewModel
import com.firstsportz.slicknewz.viewmodel.ForgotPasswordViewModelFactory

class VerificationCodeActivity : AppCompatActivity() {

    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel
    private lateinit var loadingBar: LoadingDialog


    private var hasErrorDialogBeenShown = false // Avoid multiple error dialogs
    private val authorizationToken = "Bearer 3fe0be312b6d703aa35cd0104816501f383cbfeac790d8a9d6931966d8a9a58fc5803511cf3040156d6200398c1bddf1ff92a28d18c0314719832855dc0edae0b7c3f30b6746d57659a07900ea854cc412f77c7f981833551e5a239b37d6d99ad7b4c264e5616cd3ee8fdc5c6333c70cff6aa7b972bd85df8579a5f2c798e554"

    private lateinit var errorDialog: ErrorDialog
    private lateinit var binding: ActivityVerificationcodeBinding
    private var emailId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        errorDialog = ErrorDialog(this)

        emailId=intent.extras?.getString("emailId").toString()


        // Initialize utilities
        loadingBar = LoadingDialog(this)
        errorDialog = ErrorDialog(this)

        // Set up ViewModel
        val repository = AuthRepository()
        val factory = ForgotPasswordViewModelFactory(repository)
        forgotPasswordViewModel = ViewModelProvider(this, factory)[ForgotPasswordViewModel::class.java]

        // Observe forgot password response
        observeForgotPasswordResponse()

        // Add text change listeners for OTP fields
        setupOtpInputListeners()

        // Verify button click listener
        binding.btnNext.setOnClickListener {
            val otp = getOtpInput()
            if (otp.length == 5) {
                verifyOtp(otp)
            } else {
                //Toast.makeText(this, "Please enter a valid 5-digit OTP", Toast.LENGTH_SHORT).show()
                errorDialog.showErrorDialog(message = getString(R.string.verification_code_validation))
            }
        }

        // Resend code click listener
        binding.resendCode.setOnClickListener {
            resendOtp()
        }
    }

    private fun observeForgotPasswordResponse() {
        forgotPasswordViewModel.forgotPasswordResponse.observe(this) { resource ->
            when (resource) {
                is com.firstsportz.slicknewz.ui.utils.Resource.Success -> {
                    hasErrorDialogBeenShown = false
                    loadingBar.dismiss()
                    Toast.makeText(this, getString(R.string.reset_link_sent), Toast.LENGTH_LONG).show()
                    //finish() // Navigate back to login or other appropriate screen


                }
                is com.firstsportz.slicknewz.ui.utils.Resource.Error -> {
                    if (!hasErrorDialogBeenShown) {
                        hasErrorDialogBeenShown = true
                        loadingBar.dismiss()
                        errorDialog.showErrorDialog(
                            message = resource.message ?: getString(R.string.error_message)
                        )
                    }
                }
                is com.firstsportz.slicknewz.ui.utils.Resource.Loading -> loadingBar.show()
            }
        }
    }

    private fun handleForgotPassword() {
        if (!NetworkUtil.isInternetAvailable(this)) {
            errorDialog.showErrorDialog(message = getString(R.string.no_internet_connection))
            return
        }
        val request = ForgotPasswordRequest(email = emailId)
        loadingBar.show(getString(R.string.sending_reset_link))
        hasErrorDialogBeenShown = false // Reset error dialog flag for API call
        forgotPasswordViewModel.forgotPassword(authorizationToken,request)
    }

    private fun setupOtpInputListeners() {
        binding.etOtp1.addTextChangedListener(OtpTextWatcher(binding.etOtp1, binding.etOtp2, null))
        binding.etOtp2.addTextChangedListener(OtpTextWatcher(binding.etOtp2, binding.etOtp3, binding.etOtp1))
        binding.etOtp3.addTextChangedListener(OtpTextWatcher(binding.etOtp3, binding.etOtp4, binding.etOtp2))
        binding.etOtp4.addTextChangedListener(OtpTextWatcher(binding.etOtp4, binding.etOtp5, binding.etOtp3))
        binding.etOtp5.addTextChangedListener(OtpTextWatcher(binding.etOtp5, null, binding.etOtp4))
    }

    private fun getOtpInput(): String {
        return binding.etOtp1.text.toString() +
                binding.etOtp2.text.toString() +
                binding.etOtp3.text.toString() +
                binding.etOtp4.text.toString() +
                binding.etOtp5.text.toString()
    }

    private fun verifyOtp(otp: String) {
        // Implement API call or verification logic here
        Log.d("VerificationCodeActivity", "Verifying OTP: $otp")
        val intent = Intent(this, UpdatePasswordActivity::class.java)
        intent.putExtra("verificationCode",otp)
        startActivity(intent)

    }

    private fun resendOtp() {
        // Implement resend OTP logic here
        Log.d("VerificationCodeActivity", "Resending OTP")
        handleForgotPassword()
    }

    private inner class OtpTextWatcher(
        private val currentField: androidx.appcompat.widget.AppCompatEditText,
        private val nextField: androidx.appcompat.widget.AppCompatEditText?,
        private val previousField: androidx.appcompat.widget.AppCompatEditText?
    ) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (!s.isNullOrEmpty() && s.length == 1) {
                // Change the background color to #1868DB
                changeBackgroundColor(currentField, R.color.primaryColor)
                currentField.setTextColor(getColor(R.color.white))

                // Move to the next field
                nextField?.requestFocus()
            } else if (before > 0 && s.isNullOrEmpty()) {
                // Reset the background color to default
                changeBackgroundColor(currentField, R.color.white)
                currentField.setTextColor(getColor(R.color.black))

                // Move focus back to the previous field
                previousField?.requestFocus()
            }
        }

        override fun afterTextChanged(s: Editable?) {}

        private fun changeBackgroundColor(field: androidx.appcompat.widget.AppCompatEditText, colorRes: Int) {
            val background = field.background as GradientDrawable
            background.setColor(getColor(colorRes))
            field.background = background
        }
    }
}

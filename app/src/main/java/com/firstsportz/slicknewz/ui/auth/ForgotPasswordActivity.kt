package com.firstsportz.slicknewz.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.firstsportz.slicknewz.R
import com.firstsportz.slicknewz.data.model.ForgotPasswordRequest
import com.firstsportz.slicknewz.databinding.ActivityForgotpassBinding
import com.firstsportz.slicknewz.repository.AuthRepository
import com.firstsportz.slicknewz.ui.utils.ErrorDialog
import com.firstsportz.slicknewz.ui.utils.LoadingDialog
import com.firstsportz.slicknewz.utils.NetworkUtil
import com.firstsportz.slicknewz.viewmodel.ForgotPasswordViewModel
import com.firstsportz.slicknewz.viewmodel.ForgotPasswordViewModelFactory

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotpassBinding
    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel
    private lateinit var loadingBar: LoadingDialog
    private lateinit var errorDialog: ErrorDialog

    private var hasErrorDialogBeenShown = false // Avoid multiple error dialogs
    private val authorizationToken = "Bearer 3fe0be312b6d703aa35cd0104816501f383cbfeac790d8a9d6931966d8a9a58fc5803511cf3040156d6200398c1bddf1ff92a28d18c0314719832855dc0edae0b7c3f30b6746d57659a07900ea854cc412f77c7f981833551e5a239b37d6d99ad7b4c264e5616cd3ee8fdc5c6333c70cff6aa7b972bd85df8579a5f2c798e554"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotpassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize utilities
        loadingBar = LoadingDialog(this)
        errorDialog = ErrorDialog(this)

        // Set up ViewModel
        val repository = AuthRepository()
        val factory = ForgotPasswordViewModelFactory(repository)
        forgotPasswordViewModel = ViewModelProvider(this, factory)[ForgotPasswordViewModel::class.java]

        // Observe forgot password response
        observeForgotPasswordResponse()

        // Handle "Send Reset Link" button click
        binding.btnNext.setOnClickListener { handleForgotPassword() }
    }

    private fun handleForgotPassword() {
        if (!NetworkUtil.isInternetAvailable(this)) {
            errorDialog.showErrorDialog(message = getString(R.string.no_internet_connection))
            return
        }

        val email = binding.etEmailfield.text.toString().trim()

        if (email.isBlank()) {
            errorDialog.showErrorDialog(message = getString(R.string.email_is_required))
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorDialog.showErrorDialog(message = getString(R.string.enter_valid_email))
            return
        }

        val request = ForgotPasswordRequest(email = email)
        loadingBar.show(getString(R.string.sending_reset_link))
        hasErrorDialogBeenShown = false // Reset error dialog flag for API call
        forgotPasswordViewModel.forgotPassword(authorizationToken,request)
    }

    private fun navigateToVerificationCode() {
        val email = binding.etEmailfield.text.toString().trim()
        val intent = Intent(this, VerificationCodeActivity::class.java)
        intent.putExtra("emailId",email)
        startActivity(intent)
    }
    private fun observeForgotPasswordResponse() {
        forgotPasswordViewModel.forgotPasswordResponse.observe(this) { resource ->
            when (resource) {
                is com.firstsportz.slicknewz.ui.utils.Resource.Success -> {
                    hasErrorDialogBeenShown = false
                    loadingBar.dismiss()
                    Toast.makeText(this, getString(R.string.reset_link_sent), Toast.LENGTH_LONG).show()
                    //finish() // Navigate back to login or other appropriate screen
                    navigateToVerificationCode()

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
}

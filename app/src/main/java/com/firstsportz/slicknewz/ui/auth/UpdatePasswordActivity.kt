package com.firstsportz.slicknewz.ui.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.firstsportz.slicknewz.R
import com.firstsportz.slicknewz.data.model.LoginRequest
import com.firstsportz.slicknewz.data.model.LoginResponse
import com.firstsportz.slicknewz.data.model.UpdatePasswordRequest
import com.firstsportz.slicknewz.data.model.UpdatePasswordResponse
import com.firstsportz.slicknewz.databinding.ActivityLoginBinding
import com.firstsportz.slicknewz.databinding.ActivityUpdatepassBinding
import com.firstsportz.slicknewz.repository.AuthRepository
import com.firstsportz.slicknewz.ui.utils.ErrorDialog
import com.firstsportz.slicknewz.ui.utils.LoadingDialog
import com.firstsportz.slicknewz.ui.utils.Resource
import com.firstsportz.slicknewz.utils.NetworkUtil
import com.firstsportz.slicknewz.viewmodel.UpdatePasswordViewModel
import com.firstsportz.slicknewz.viewmodel.UpdatePasswordViewModelFactory
import org.jetbrains.annotations.NotNull

class UpdatePasswordActivity  : AppCompatActivity()  {

    private lateinit var binding: ActivityUpdatepassBinding
    private lateinit var updatePasswordViewModel: UpdatePasswordViewModel
    private lateinit var loadingBar: LoadingDialog
    private lateinit var errorDialog: ErrorDialog

    private val authorizationToken = "Bearer 3fe0be312b6d703aa35cd0104816501f383cbfeac790d8a9d6931966d8a9a58fc5803511cf3040156d6200398c1bddf1ff92a28d18c0314719832855dc0edae0b7c3f30b6746d57659a07900ea854cc412f77c7f981833551e5a239b37d6d99ad7b4c264e5616cd3ee8fdc5c6333c70cff6aa7b972bd85df8579a5f2c798e554"
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    private var hasErrorDialogBeenShown = false // Flag to avoid multiple error dialogs
    private var verificationCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatepassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize utilities
        loadingBar = LoadingDialog(this)
        errorDialog = ErrorDialog(this)

        verificationCode = intent.extras?.getString("verificationCode") ?: ""
        if (verificationCode.isBlank()) {
            errorDialog.showErrorDialog("Error", "Verification code is missing.")
            return
        }
        Log.d("UpdatePasswordActivity", "Verification Code: $verificationCode")

        // Set up ViewModel
        val repository = AuthRepository()
        val factory = UpdatePasswordViewModelFactory(repository)
        updatePasswordViewModel = ViewModelProvider(this, factory)[UpdatePasswordViewModel::class.java]

        // Observe LoginResponse
        observeUpdatePasswordResponse()


        // Handle Login button click
        binding.btnSubmit.setOnClickListener { handleLogin() }



        // Initialize Views
        val etPassword: EditText = findViewById(R.id.etPassword)
        val ivTogglePassword: ImageView = findViewById(R.id.ivTogglePassword)

        val etConfirmPassword: EditText = findViewById(R.id.etConfirmPassword)
        val ivToggleConfirmPassword: ImageView = findViewById(R.id.ivToggleConfirmPassword)

        // Toggle Password Visibility
        ivTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            togglePasswordVisibility(etPassword, ivTogglePassword, isPasswordVisible)
        }

        // Toggle Confirm Password Visibility
        ivToggleConfirmPassword.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            togglePasswordVisibility(etConfirmPassword, ivToggleConfirmPassword, isConfirmPasswordVisible)
        }


    }


    private fun togglePasswordVisibility(editText: EditText, imageView: ImageView, isVisible: Boolean) {
        if (isVisible) {
            // Show password and change icon to "open eye"
            editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            imageView.setImageResource(R.drawable.ic_open_eye)
        } else {
            // Hide password and change icon to "closed eye"
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            imageView.setImageResource(R.drawable.ic_eye)
        }
        editText.setSelection(editText.text.length)
    }


    private fun handleLogin() {
        if (!NetworkUtil.isInternetAvailable(this)) {
            errorDialog.showErrorDialog(message = getString(R.string.no_internet_connection))
            return
        }

        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (password != confirmPassword) {
            if (!hasErrorDialogBeenShown) {
                hasErrorDialogBeenShown = true
                errorDialog.showErrorDialog(message = getString(R.string.password_mismatch))
            }
            return
        }

        if (validateLoginFields(password, confirmPassword)) {
            val request = UpdatePasswordRequest(
                code = verificationCode,
                password = password,
                passwordConfirmation = confirmPassword
            )
            loadingBar.show(getString(R.string.update_password_msg))
            hasErrorDialogBeenShown = false // Reset the flag for the API call
            updatePasswordViewModel.updatePasswordUser(authorizationToken,request)
        }
    }

    private fun validateLoginFields(password: String, confirmPassword: String): Boolean {
        if (password.isBlank()) {
            errorDialog.showErrorDialog(message = getString(R.string.password_required))
            return false
        }

        if (confirmPassword.isBlank()) {
            errorDialog.showErrorDialog(message = getString(R.string.confirmpassword_required))
            return false
        }

        if (password.length < 6) {
            errorDialog.showErrorDialog(message = getString(R.string.password_length))
            return false
        }
        return true
    }

    private fun observeUpdatePasswordResponse() {
        updatePasswordViewModel.updatePasswordResponse.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    hasErrorDialogBeenShown = false
                    loadingBar.dismiss()
                    handleSuccessResponse(resource.data)
                }
                is Resource.Error -> {
                    if (!hasErrorDialogBeenShown) {
                        hasErrorDialogBeenShown = true
                        loadingBar.dismiss()
                        handleErrorResponse(resource.message)

                    }
                }
                is Resource.Loading -> loadingBar.show()
            }
        }
    }

    private fun handleSuccessResponse(data: Any?) {
        if (data is UpdatePasswordResponse) {
            errorDialog.showErrorDialog(
                title = "Success",
                message = "Password Updated Successfully. Please login.",
                onDismiss = {
                    navigateToLoginScreen()
                }
            )
        } else {
            errorDialog.showErrorDialog(message = "Unexpected response format.")
        }
    }

    private fun handleErrorResponse(data: String?) {
      try{
            val errorMessage = data?: "An error occurred."

            if (errorMessage == "Incorrect code provided") {
                errorDialog.showErrorDialog(
                    title = "Error",
                    message = errorMessage,
                    onDismiss = {
                        finish() // Finish the current activity
                    }
                )
            } else {
                errorDialog.showErrorDialog(message = errorMessage)
            }
        } catch(ex:Exception){
            errorDialog.showErrorDialog(message = "Unexpected response format.")
        }
    }


    private fun navigateToLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Ensure the current activity is finished
    }

}
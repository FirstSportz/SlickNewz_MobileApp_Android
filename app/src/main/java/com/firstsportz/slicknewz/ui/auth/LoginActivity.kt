package com.firstsportz.slicknewz.ui.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.firstsportz.slicknewz.R
import com.firstsportz.slicknewz.data.model.LoginRequest
import com.firstsportz.slicknewz.data.model.LoginResponse
import com.firstsportz.slicknewz.databinding.ActivityLoginBinding
import com.firstsportz.slicknewz.repository.AuthRepository
import com.firstsportz.slicknewz.ui.utils.Resource
import com.firstsportz.slicknewz.ui.utils.ErrorDialog
import com.firstsportz.slicknewz.ui.utils.LoadingDialog
import com.firstsportz.slicknewz.utils.NetworkUtil
import com.firstsportz.slicknewz.viewmodel.LoginViewModel
import com.firstsportz.slicknewz.viewmodel.LoginViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var loadingBar: LoadingDialog
    private lateinit var errorDialog: ErrorDialog
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences: SharedPreferences

    private val PREF_NAME = "LoginPreferences"
    private val authorizationToken = "Bearer 3fe0be312b6d703aa35cd0104816501f383cbfeac790d8a9d6931966d8a9a58fc5803511cf3040156d6200398c1bddf1ff92a28d18c0314719832855dc0edae0b7c3f30b6746d57659a07900ea854cc412f77c7f981833551e5a239b37d6d99ad7b4c264e5616cd3ee8fdc5c6333c70cff6aa7b972bd85df8579a5f2c798e554"
    private var isPasswordVisible = false
    private var hasErrorDialogBeenShown = false // Flag to avoid multiple error dialogs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize utilities
        loadingBar = LoadingDialog(this)
        errorDialog = ErrorDialog(this)
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Set up ViewModel
        val repository = AuthRepository()
        val factory = LoginViewModelFactory(repository)
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        // Observe LoginResponse
        observeLoginResponse()

        // Load saved credentials if Remember Me is checked
        loadSavedCredentials()

        // Handle Login button click
        binding.btnSignIn.setOnClickListener { handleLogin() }

        // Save credentials on Remember Me checkbox
        binding.termsCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) saveCredentials() else clearSavedCredentials()
        }

        // Handle Forgot Password click
        binding.forgotPasswordText.setOnClickListener { navigateToForgotPassword() }

        // Toggle Password Visibility
        val etPassword: EditText = binding.etPassword
        val ivTogglePassword: ImageView = binding.ivTogglePassword
        ivTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            togglePasswordVisibility(etPassword, ivTogglePassword, isPasswordVisible)
        }

        // Set up Google Sign-In
        setupGoogleSignIn()
        binding.btnGoogleSignIn.setOnClickListener { initiateGoogleSignIn() }

        binding.createAccount.setOnClickListener { showSignUpScreen() }
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

    private fun loadSavedCredentials() {
        val savedEmail = sharedPreferences.getString("email", "")
        val savedPassword = sharedPreferences.getString("password", "")

        if (!savedEmail.isNullOrBlank() && !savedPassword.isNullOrBlank()) {
            binding.etEmailfield.setText(savedEmail)
            binding.etPassword.setText(savedPassword)
            binding.termsCheckbox.isChecked = true
        }
    }

    private fun saveCredentials() {
        val editor = sharedPreferences.edit()
        editor.putString("email", binding.etEmailfield.text.toString())
        editor.putString("password", binding.etPassword.text.toString())
        editor.apply()
    }

    private fun clearSavedCredentials() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun navigateToForgotPassword() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }
    private fun initiateGoogleSignIn() {
        if (!NetworkUtil.isInternetAvailable(this)) {
            errorDialog.showErrorDialog(message = getString(R.string.no_internet_connection))
            return
        }
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun showSignUpScreen() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleGoogleSignInResult(task)
        }

    private fun handleGoogleSignInResult(task: com.google.android.gms.tasks.Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                Log.d("GoogleSignIn", "Signed in as: ${account.email}")
                handleGoogleLogin(account)
            }
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Sign-in failed: ${e.message}")
            errorDialog.showErrorDialog(
                message = "Google Sign-In failed: ${e.message}"
            )
        }
    }

    private fun handleGoogleLogin(account: GoogleSignInAccount) {
        val request = LoginRequest(
            identifier = account.email ?: "",
            password = "", // OAuth-based login doesn't require password
            deviceToken = null,
            deviceOS = "android"
        )

        loadingBar.show(getString(R.string.sign_in_google_txt))
        loginViewModel.loginUser(authorizationToken,request)
    }

    private fun handleLogin() {
        if (!NetworkUtil.isInternetAvailable(this)) {
            errorDialog.showErrorDialog(message = getString(R.string.no_internet_connection))
            return
        }

        val email = binding.etEmailfield.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (validateLoginFields(email, password)) {
            val request = LoginRequest(
                identifier = email,
                password = password,
                deviceToken = null,
                deviceOS = "android"
            )
            loadingBar.show(getString(R.string.logging_in))
            hasErrorDialogBeenShown = false // Reset the flag for the API call
            loginViewModel.loginUser(authorizationToken,request)
        }
    }

    private fun validateLoginFields(email: String, password: String): Boolean {
        if (email.isBlank()) {
            errorDialog.showErrorDialog(message = getString(R.string.email_is_required))
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorDialog.showErrorDialog(message = getString(R.string.enter_valid_email))
            return false
        }
        if (password.isBlank()) {
            errorDialog.showErrorDialog(message = getString(R.string.password_required))
            return false
        }
        return true
    }

    private fun observeLoginResponse() {
        loginViewModel.loginResponse.observe(this) { resource ->
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
                        errorDialog.showErrorDialog(
                            message = resource.message ?: "An error occurred."
                        )
                    }
                }
                is Resource.Loading -> loadingBar.show()
            }
        }
    }

    private fun handleSuccessResponse(data: Any?) {
        if (data is LoginResponse) {
            errorDialog.showErrorDialog("Success", "Welcome ${data.user?.username}!")
            val editor = sharedPreferences.edit()
            editor.putString("jwt",data.jwt)
            editor.putString("userName",data.user?.username)

            if(!sharedPreferences.contains("isProfileComplete"))
                editor.putBoolean("isProfileComplete",false)

            editor.apply()

            if(!sharedPreferences.getBoolean("isProfileComplete",false))
                navigateToChooseCategoryActivity()
            // Navigate to the main screen
        } else {
            errorDialog.showErrorDialog(message = "Unexpected response format.")
        }
    }

    private fun navigateToChooseCategoryActivity() {
        val intent = Intent(this, ChooseInterestActivity::class.java)
        startActivity(intent)
    }
}

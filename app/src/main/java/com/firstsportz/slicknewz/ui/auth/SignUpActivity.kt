package com.firstsportz.slicknewz.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.firstsportz.slicknewz.R
import com.firstsportz.slicknewz.data.model.SignUpRequest
import com.firstsportz.slicknewz.data.model.SignUpResponse
import com.firstsportz.slicknewz.databinding.ActivitySignUpBinding
import com.firstsportz.slicknewz.repository.AuthRepository
import com.firstsportz.slicknewz.ui.utils.Resource
import com.firstsportz.slicknewz.ui.utils.ErrorDialog
import com.firstsportz.slicknewz.ui.utils.LoadingDialog
import com.firstsportz.slicknewz.utils.NetworkUtil
import com.firstsportz.slicknewz.viewmodel.SignUpViewModel
import com.firstsportz.slicknewz.viewmodel.SignUpViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var loadingBar: LoadingDialog
    private lateinit var errorDialog: ErrorDialog
    private lateinit var googleSignInClient: GoogleSignInClient

    private val authorizationToken = "Bearer 3fe0be312b6d703aa35cd0104816501f383cbfeac790d8a9d6931966d8a9a58fc5803511cf3040156d6200398c1bddf1ff92a28d18c0314719832855dc0edae0b7c3f30b6746d57659a07900ea854cc412f77c7f981833551e5a239b37d6d99ad7b4c264e5616cd3ee8fdc5c6333c70cff6aa7b972bd85df8579a5f2c798e554"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize utilities
        loadingBar = LoadingDialog(this)
        errorDialog = ErrorDialog(this)

        // Set up ViewModel
        val repository = AuthRepository()
        val factory = SignUpViewModelFactory(repository)
        signUpViewModel = ViewModelProvider(this, factory)[SignUpViewModel::class.java]

        // Set up Google Sign-In
        setupGoogleSignIn()

        // Handle Sign-Up button click
        binding.btnSignUp.setOnClickListener { handleSignUp() }

        // Handle Google Sign-In button click
        binding.btnGoogleSignIn.setOnClickListener { initiateGoogleSignIn() }
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun initiateGoogleSignIn() {
        if (!NetworkUtil.isInternetAvailable(this)) {
            errorDialog.showErrorDialog(message = getString(R.string.no_internet_connection))
            return
        }

        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
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
                // Successfully signed in
                Log.d("GoogleSignIn", "Signed in as: ${account.email}")
                handleGoogleSignUp(account)
            }
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Sign-in failed: ${e.message}")
            errorDialog.showErrorDialog(
                message = "Google Sign-In failed: ${e.message}"
            )
        }
    }

    private fun handleGoogleSignUp(account: GoogleSignInAccount) {
        val request = SignUpRequest(
            email = account.email ?: "",
            username = account.displayName ?: "",
            password = account.idToken.toString(), // OAuth-based login doesn't require password
            phoneNumber = 1234,
            categories = listOf(),
            deviceToken = null,
            deviceOS = "android"
        )

        loadingBar.show("Signing up with Google...")
        signUpViewModel.registerUser(authorizationToken, request)

        signUpViewModel.signUpResponse.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    loadingBar.dismiss()
                    Toast.makeText(
                        this,
                        "Welcome ${account.displayName}!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Error -> {
                    loadingBar.dismiss()
                    errorDialog.showErrorDialog(
                        message = resource.message ?: "An error occurred."
                    )
                }
                is Resource.Loading -> loadingBar.show()
            }
        }
    }

    private fun handleSignUp() {
        if (!NetworkUtil.isInternetAvailable(this)) {
            errorDialog.showErrorDialog(message = getString(R.string.no_internet_connection))
            return
        }

        val email = binding.etEmailfield.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        if (password != confirmPassword) {
            errorDialog.showErrorDialog(message = getString(R.string.password_mismatch))
            return
        }

        if (validateSignUpFields(email, password, confirmPassword)) {
            val request = SignUpRequest(
                email = email,
                username = email.split("@")[0],
                password = password,
                phoneNumber = 1234,
                categories = listOf(1, 2),
                deviceToken = null,
                deviceOS = "android"
            )

            loadingBar.show("Registering...")
            signUpViewModel.registerUser(authorizationToken, request)

            signUpViewModel.signUpResponse.observe(this) { resource ->
                when (resource) {
                    is Resource.Success -> {
                        loadingBar.dismiss()
                        handleSuccessResponse(resource.data)
                    }
                    is Resource.Error -> {
                        loadingBar.dismiss()
                        errorDialog.showErrorDialog(
                            message = resource.message ?: "An error occurred."
                        )
                    }
                    is Resource.Loading -> loadingBar.show()
                }
            }
        }
    }

    private fun validateSignUpFields(
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (email.isBlank()) {
            errorDialog.showErrorDialog(message = "Email is required.")
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorDialog.showErrorDialog(message = "Enter a valid email address.")
            return false
        }
        if (password.isBlank()) {
            errorDialog.showErrorDialog(message = "Password is required.")
            return false
        }
        if (password.length < 6) {
            errorDialog.showErrorDialog(message = "Password must be at least 6 characters long.")
            return false
        }
        return true
    }

    private fun handleSuccessResponse(data: Any?) {
        if (data is SignUpResponse) {
            val username = data.user?.username ?: "User"
            errorDialog.showErrorDialog("Success", "Welcome $username!")
            // Navigate to another screen or perform any action
        } else {
            errorDialog.showErrorDialog(message = "Unexpected response format.")
        }
    }
}

package com.firstsportz.slicknewz.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstsportz.slicknewz.data.model.ErrorResponse
import com.firstsportz.slicknewz.data.model.LoginRequest
import com.firstsportz.slicknewz.data.model.LoginResponse
import com.firstsportz.slicknewz.data.model.SignUpRequest
import com.firstsportz.slicknewz.data.model.SignUpResponse
import com.firstsportz.slicknewz.repository.AuthRepository
import com.firstsportz.slicknewz.ui.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    val loginResponse = MutableLiveData<Resource<LoginResponse>>()

    fun loginUser(authHeader: String, request: LoginRequest) {
        loginResponse.value = Resource.Loading()

        viewModelScope.launch {
            try {
                val response = repository.login(authHeader, request)
                handleResponse(response)
            } catch (e: Exception) {
                // Exception handling
                Log.e("SignUp", "Exception: ${e.message}")
                loginResponse.postValue(Resource.Error("Exception: ${e.message}"))
            }
        }
    }

    private fun handleResponse(response: Response<LoginResponse>) {
        if (response.isSuccessful) {
            val body = response.body()
            if (body?.user != null) {
                // Successful response with a valid user
                Log.d("Login", "Success: $body")
                loginResponse.postValue(Resource.Success(body))
            } else {
                // Successful response but missing user or unexpected format
                Log.e("Login", "Error: Missing or invalid success response body")
                loginResponse.postValue(Resource.Error("Error: Invalid success response format"))
            }
        } else {
            // Handle error response
            val errorBody = response.errorBody()?.string()
            Log.e("Login", "Error Body: $errorBody")
            try {
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                val errorMessage = errorResponse.error?.message ?: "Unknown error occurred"
                loginResponse.postValue(Resource.Error("$errorMessage"))
            } catch (e: Exception) {
                // Fallback for parsing failure
                Log.e("Login", "Error parsing error body: ${e.message}")
                loginResponse.postValue(Resource.Error("${response.message()}"))
            }
        }
    }
}

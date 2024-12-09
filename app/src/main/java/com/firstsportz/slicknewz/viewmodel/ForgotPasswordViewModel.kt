package com.firstsportz.slicknewz.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstsportz.slicknewz.data.model.ErrorResponse
import com.firstsportz.slicknewz.data.model.ForgotPasswordRequest
import com.firstsportz.slicknewz.data.model.ForgotPasswordResponse
import com.firstsportz.slicknewz.repository.AuthRepository
import com.firstsportz.slicknewz.ui.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Response

class ForgotPasswordViewModel(private val repository: AuthRepository) : ViewModel() {

    val forgotPasswordResponse = MutableLiveData<Resource<ForgotPasswordResponse>>()

    fun forgotPassword(authHeader: String, request: ForgotPasswordRequest) {
        forgotPasswordResponse.value = Resource.Loading()

        viewModelScope.launch {
            try {
                val response = repository.forgotPassword(authHeader, request)
                handleResponse(response)
            } catch (e: Exception) {
                // Exception handling
                Log.e("ForgotPassword", "Exception: ${e.message}")
                forgotPasswordResponse.postValue(Resource.Error("Exception: ${e.message}"))
            }
        }
    }

    private fun handleResponse(response: Response<ForgotPasswordResponse>) {
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                // Successful response with valid data
                Log.d("ForgotPassword", "Success: $body")
                forgotPasswordResponse.postValue(Resource.Success(body))
            } else {
                // Successful response but missing or unexpected format
                Log.e("ForgotPassword", "Error: Missing or invalid success response body")
                forgotPasswordResponse.postValue(Resource.Error("Error: Invalid success response format"))
            }
        } else {
            // Handle error response
            val errorBody = response.errorBody()?.string()
            Log.e("ForgotPassword", "Error Body: $errorBody")
            try {
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                val errorMessage = errorResponse.error?.message ?: "Unknown error occurred"
                forgotPasswordResponse.postValue(Resource.Error("$errorMessage"))
            } catch (e: Exception) {
                // Fallback for parsing failure
                Log.e("ForgotPassword", "Error parsing error body: ${e.message}")
                forgotPasswordResponse.postValue(Resource.Error("${response.message()}"))
            }
        }
    }
}

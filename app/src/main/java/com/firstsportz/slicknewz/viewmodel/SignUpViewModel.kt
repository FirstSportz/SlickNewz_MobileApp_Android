package com.firstsportz.slicknewz.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstsportz.slicknewz.data.model.SignUpRequest
import com.firstsportz.slicknewz.data.model.SignUpResponse
import com.firstsportz.slicknewz.data.model.ErrorResponse
import com.firstsportz.slicknewz.repository.AuthRepository
import com.firstsportz.slicknewz.ui.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Response

class SignUpViewModel(private val repository: AuthRepository) : ViewModel() {

    val signUpResponse: MutableLiveData<Resource<SignUpResponse>> = MutableLiveData()

    fun registerUser(authHeader: String, request: SignUpRequest) {
        signUpResponse.value = Resource.Loading()

        viewModelScope.launch {
            try {
                val response = repository.registerUser(authHeader, request)
                handleResponse(response)
            } catch (e: Exception) {
                // Exception handling
                Log.e("SignUp", "Exception: ${e.message}")
                signUpResponse.postValue(Resource.Error("Exception: ${e.message}"))
            }
        }
    }

    private fun handleResponse(response: Response<SignUpResponse>) {
        if (response.isSuccessful) {
            val body = response.body()
            if (body?.user != null) {
                // Successful response with a valid user
                Log.d("SignUp", "Success: $body")
                signUpResponse.postValue(Resource.Success(body))
            } else {
                // Successful response but missing user or unexpected format
                Log.e("SignUp", "Error: Missing or invalid success response body")
                signUpResponse.postValue(Resource.Error("Error: Invalid success response format"))
            }
        } else {
            // Handle error response
            val errorBody = response.errorBody()?.string()
            Log.e("SignUp", "Error Body: $errorBody")
            try {
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                val errorMessage = errorResponse.error?.message ?: "Unknown error occurred"
                signUpResponse.postValue(Resource.Error("$errorMessage"))
            } catch (e: Exception) {
                // Fallback for parsing failure
                Log.e("SignUp", "Error parsing error body: ${e.message}")
                signUpResponse.postValue(Resource.Error("${response.message()}"))
            }
        }
    }
}

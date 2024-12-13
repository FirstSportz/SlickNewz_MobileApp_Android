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
import com.firstsportz.slicknewz.data.model.UpdatePasswordRequest
import com.firstsportz.slicknewz.data.model.UpdatePasswordResponse
import com.firstsportz.slicknewz.repository.AuthRepository
import com.firstsportz.slicknewz.ui.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Response

class UpdatePasswordViewModel(private val repository: AuthRepository) : ViewModel() {

    val updatePasswordResponse = MutableLiveData<Resource<UpdatePasswordResponse>>()

    fun updatePasswordUser(authHeader: String, request: UpdatePasswordRequest) {
        updatePasswordResponse.value = Resource.Loading()

        viewModelScope.launch {
            try {
                val response = repository.updatePassword(authHeader, request)
                handleResponse(response)
            } catch (e: Exception) {
                // Exception handling
                Log.e("Update Password", "Exception: ${e.message}")
                updatePasswordResponse.postValue(Resource.Error("Exception: ${e.message}"))
            }
        }
    }

    private fun handleResponse(response: Response<UpdatePasswordResponse>) {
        if (response.isSuccessful) {
            Log.d("Update Password", "Response Body: ${response.body()}")
            val body = response.body()
            if (body != null && body.jwt != null && body.user != null) {
                // Successful response with a valid user
                Log.d("Update Password", "Success: $body")
                updatePasswordResponse.postValue(Resource.Success(body))
            } else {
                // Successful response but missing user or unexpected format
                Log.e("Update Password", "Error: Missing or invalid success response body")
                updatePasswordResponse.postValue(Resource.Error("Error: Invalid success response format"))
            }
        } else {
            // Handle error response
            val errorBody = response.errorBody()?.string()
            Log.e("Update Password", "Error Body: $errorBody")
            try {
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                val errorMessage = errorResponse.error?.message ?: "Unknown error occurred"
                updatePasswordResponse.postValue(Resource.Error("$errorMessage"))
            } catch (e: Exception) {
                // Fallback for parsing failure
                Log.e("Update Password", "Error parsing error body: ${e.message}")
                updatePasswordResponse.postValue(Resource.Error("${response.message()}"))
            }
        }
    }
}

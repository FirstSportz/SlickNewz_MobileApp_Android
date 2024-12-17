package com.firstsportz.slicknewz.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstsportz.slicknewz.data.model.CategoryResponse
import com.firstsportz.slicknewz.repository.AuthRepository
import com.firstsportz.slicknewz.ui.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class CategoryViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _categoryResponse = MutableLiveData<Resource<CategoryResponse>>()
    val categoryResponse: LiveData<Resource<CategoryResponse>> = _categoryResponse

    fun fetchCategories(authHeader: String) {
        _categoryResponse.value = Resource.Loading()

        viewModelScope.launch {
            try {
                val response = repository.getCategories(authHeader)
                handleResponse(response)
            } catch (e: Exception) {
                Log.e("CategoryViewModel", "Exception: ${e.message}")
                _categoryResponse.postValue(Resource.Error("Exception: ${e.message}"))
            }
        }
    }

    private fun handleResponse(response: Response<CategoryResponse>) {
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                // Non-null response body
                Log.d("CategoryViewModel", "Categories fetched successfully: $body")
                _categoryResponse.postValue(Resource.Success(body))
            } else {
                // Null response body
                Log.e("CategoryViewModel", "Error: Null response body")
                _categoryResponse.postValue(Resource.Error("Unexpected empty response"))
            }
        } else {
            // Error response
            val errorBody = response.errorBody()?.string()
            Log.e("CategoryViewModel", "Error Body: $errorBody")
            _categoryResponse.postValue(Resource.Error("Failed to fetch categories"))
        }
    }
}

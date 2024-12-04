package com.firstsportz.slicknewz.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstsportz.slicknewz.data.model.LoginRequest
import com.firstsportz.slicknewz.data.model.LoginResponse
import com.firstsportz.slicknewz.repository.AuthRepository
import com.firstsportz.slicknewz.ui.utils.Resource
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginResponse = MutableLiveData<Resource<LoginResponse>>()
    val loginResponse: LiveData<Resource<LoginResponse>> get() = _loginResponse

    fun loginUser(authorizationToken: String, loginRequest: LoginRequest) {
        _loginResponse.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
                val response = repository.login(authorizationToken, loginRequest)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _loginResponse.postValue(Resource.Success(responseBody))
                    } else {
                        _loginResponse.postValue(Resource.Error("Response body is null"))
                    }
                } else {
                    _loginResponse.postValue(Resource.Error(response.message()))
                }
            } catch (e: Exception) {
                _loginResponse.postValue(Resource.Error(e.message ?: "An error occurred"))
            }
        }
    }
}

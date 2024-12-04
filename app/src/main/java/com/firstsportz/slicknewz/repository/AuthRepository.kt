package com.firstsportz.slicknewz.repository

import com.firstsportz.slicknewz.data.model.LoginRequest
import com.firstsportz.slicknewz.data.model.SignUpRequest
import com.firstsportz.slicknewz.data.network.RetrofitClient

class AuthRepository {
    suspend fun registerUser(authHeader: String, request: SignUpRequest) =
        RetrofitClient.instance.registerUser(authHeader, request)

    suspend fun login(authorizationToken: String, loginRequest: LoginRequest) =
        RetrofitClient.instance.login(authorizationToken, loginRequest)
}
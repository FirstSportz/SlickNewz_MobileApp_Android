package com.firstsportz.slicknewz.repository

import com.firstsportz.slicknewz.data.model.SignUpRequest
import com.firstsportz.slicknewz.data.network.RetrofitClient

class AuthRepository {
    suspend fun registerUser(authHeader: String, request: SignUpRequest) =
        RetrofitClient.instance.registerUser(authHeader, request)
}
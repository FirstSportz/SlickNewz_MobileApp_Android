package com.firstsportz.slicknewz.repository

import com.firstsportz.slicknewz.data.model.ForgotPasswordRequest
import com.firstsportz.slicknewz.data.model.LoginRequest
import com.firstsportz.slicknewz.data.model.NewsResponse
import com.firstsportz.slicknewz.data.model.SignUpRequest
import com.firstsportz.slicknewz.data.model.UpdatePasswordRequest
import com.firstsportz.slicknewz.data.network.RetrofitClient
import retrofit2.Response

class AuthRepository {
    suspend fun registerUser(authHeader: String, request: SignUpRequest) =
        RetrofitClient.instance.registerUser(authHeader, request)

    suspend fun login(authorizationToken: String, loginRequest: LoginRequest) =
        RetrofitClient.instance.login(authorizationToken, loginRequest)

    suspend fun forgotPassword(authorizationToken: String,forgotPasswordRequest: ForgotPasswordRequest) =
        RetrofitClient.instance.forgotPassword(authorizationToken,forgotPasswordRequest)

    suspend fun updatePassword(authorizationToken: String,updatePasswordRequest: UpdatePasswordRequest) =
        RetrofitClient.instance.updatePassword(authorizationToken,updatePasswordRequest)

    suspend fun getCategories(authHeader: String) =
        RetrofitClient.instance.getCategories(authHeader)

    suspend fun fetchNews(authorization: String, url: String): Response<NewsResponse> {
        return RetrofitClient.instance.fetchNews(url,authorization)
    }
}
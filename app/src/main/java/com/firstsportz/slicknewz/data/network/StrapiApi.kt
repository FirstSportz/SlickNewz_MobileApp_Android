package com.firstsportz.slicknewz.data.network

import com.firstsportz.slicknewz.data.model.ForgotPasswordRequest
import com.firstsportz.slicknewz.data.model.ForgotPasswordResponse
import com.firstsportz.slicknewz.data.model.LoginRequest
import com.firstsportz.slicknewz.data.model.LoginResponse
import com.firstsportz.slicknewz.data.model.SignUpRequest
import com.firstsportz.slicknewz.data.model.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface StrapiApi {
    @POST("api/auth/local/register")
    suspend fun registerUser(
        @Header("Authorization") authHeader: String,
        @Body request: SignUpRequest
    ): Response<SignUpResponse>

    @POST("auth/login")
    suspend fun login(
        @Header("Authorization") authorizationToken: String,
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ForgotPasswordResponse
}
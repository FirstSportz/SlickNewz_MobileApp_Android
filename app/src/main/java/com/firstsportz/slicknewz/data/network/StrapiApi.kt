package com.firstsportz.slicknewz.data.network

import com.firstsportz.slicknewz.data.model.CategoryResponse
import com.firstsportz.slicknewz.data.model.ForgotPasswordRequest
import com.firstsportz.slicknewz.data.model.ForgotPasswordResponse
import com.firstsportz.slicknewz.data.model.LoginRequest
import com.firstsportz.slicknewz.data.model.LoginResponse
import com.firstsportz.slicknewz.data.model.SignUpRequest
import com.firstsportz.slicknewz.data.model.SignUpResponse
import com.firstsportz.slicknewz.data.model.UpdatePasswordRequest
import com.firstsportz.slicknewz.data.model.UpdatePasswordResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface StrapiApi {
    @POST("api/auth/local/register")
    suspend fun registerUser(
        @Header("Authorization") authHeader: String,
        @Body request: SignUpRequest
    ): Response<SignUpResponse>

    @POST("api/auth/local")
    suspend fun login(
        @Header("Authorization") authorizationToken: String,
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword( @Header("Authorization") authorizationToken: String,
                                @Body request: ForgotPasswordRequest): Response<ForgotPasswordResponse>


    @POST("/api/auth/reset-password")
    suspend fun updatePassword( @Header("Authorization") authorizationToken: String,
                                @Body request: UpdatePasswordRequest): Response<UpdatePasswordResponse>


    @GET("api/categories")
    suspend fun getCategories(
        @Header("Authorization") authHeader: String
    ): Response<CategoryResponse>
}
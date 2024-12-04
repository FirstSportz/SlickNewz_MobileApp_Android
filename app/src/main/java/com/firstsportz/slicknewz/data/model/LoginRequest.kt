package com.firstsportz.slicknewz.data.model

data class LoginRequest(
    val email: String,
    val password: String,
    val deviceToken: String?,
    val deviceOS: String
)

package com.firstsportz.slicknewz.data.model

data class LoginRequest(
    val identifier: String,
    val password: String,
    val deviceToken: String?,
    val deviceOS: String
)

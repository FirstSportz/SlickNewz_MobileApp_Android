package com.firstsportz.slicknewz.data.model

data class SignUpRequest(
    val email: String,
    val username: String,
    val password: String,
    val phoneNumber: Int,
    val categories: List<Int>,
    val deviceToken: String?,
    val deviceOS: String?
)
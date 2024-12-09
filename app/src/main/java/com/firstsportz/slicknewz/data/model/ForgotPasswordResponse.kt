package com.firstsportz.slicknewz.data.model

data class ForgotPasswordResponse(
    val message: String?,
    val error: ForgotPasswordError?
)

data class ForgotPasswordError(
    val status: Int,
    val name: String,
    val message: String,
    val details: Any?
)

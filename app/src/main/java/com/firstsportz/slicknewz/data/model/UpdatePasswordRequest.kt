package com.firstsportz.slicknewz.data.model

data class UpdatePasswordRequest (
    val code: String,
    val password: String,
    val passwordConfirmation: String
)
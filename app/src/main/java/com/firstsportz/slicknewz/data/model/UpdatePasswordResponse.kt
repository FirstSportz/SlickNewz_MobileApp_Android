package com.firstsportz.slicknewz.data.model

data class UpdatePasswordResponse (
    val jwt: String?, // JWT token for authentication
    val user: User?, // User details on success
    val error: ErrorResponse? // Error details on failure
)

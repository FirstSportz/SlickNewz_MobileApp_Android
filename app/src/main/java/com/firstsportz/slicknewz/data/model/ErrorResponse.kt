package com.firstsportz.slicknewz.data.model

data class ErrorResponse(
    val data: Any?, // Use `Any?` if the data field is dynamic or null
    val error: ErrorDetail?
)

data class ErrorDetail(
    val status: Int,
    val name: String,
    val message: String,
    val details: Map<String, Any>?
)

package com.firstsportz.slicknewz.data.model

data class SignUpResponse(
    val user: User?
)

data class User(
    val id: Int,
    val documentId: String?,
    val username: String,
    val email: String,
    val phoneNumber: String?,
    val deviceToken: String?,
    val deviceOS: String?,
    val role: Role?
)

data class Role(
    val id: Int,
    val documentId: String?,
    val name: String,
    val description: String?,
    val type: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val publishedAt: String?,
    val locale: String?
)

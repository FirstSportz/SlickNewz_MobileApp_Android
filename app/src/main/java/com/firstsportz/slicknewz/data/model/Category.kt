package com.firstsportz.slicknewz.data.model

data class Category(
    val id: Int,
    val documentId: String,
    val name: String,
    val slug: String,
    val description: String?,
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String,
    var isSelected: Boolean = false
)

package com.firstsportz.slicknewz.data.model

data class CategoryResponse(
    val data: List<Category>,
    val meta: Pagination
)

data class Pagination(
    val page: Int,
    val pageSize: Int,
    val pageCount: Int,
    val total: Int
)
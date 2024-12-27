package com.firstsportz.slicknewz.data.model

data class NewsResponse(
    val data: List<NewsData>,
    val meta: Meta
)

data class NewsData(
    val id: Int,
    val documentId: String,
    val title: String,
    val description: String,
    val slug: String,
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String,
    val cover: Cover,
    val author: Author,
    val category: Category
)

data class Cover(
    val id: Int,
    val url: String,
    val thumbnail: Format
)

data class Format(
    val url: String
)

data class Author(
    val id: Int,
    val name: String,
    val email: String
)

data class Meta(
    val pagination: Pagination
)


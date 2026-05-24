package org.example.dto

import java.time.LocalDateTime

data class CreatePostRequest(
    val title: String,
    val content: String,
    val authorId: Long
)

data class UpdatePostRequest(
    val title: String,
    val content: String
)

data class PostResponse(
    val id: Long,
    val title: String,
    val content: String,
    val authorId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

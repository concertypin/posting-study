package org.example.model

import java.time.LocalDateTime

data class Post(
    val id: Long,
    val title: String,
    val content: String,
    val authorId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

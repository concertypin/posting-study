package org.example.model

import java.time.LocalDateTime

data class User(
    val id: Long,
    val username: String,
    val password: String,
    val nickname: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime = createdAt
)

package org.example.dto

import java.time.LocalDateTime

data class CreateUserRequest(
    val username: String,
    val nickname: String
)

data class UpdateUserRequest(
    val nickname: String
)

data class UserResponse(
    val id: Long,
    val username: String,
    val nickname: String,
    val createdAt: LocalDateTime
)

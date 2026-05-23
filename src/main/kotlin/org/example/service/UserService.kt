package org.example.service

import org.example.dto.CreateUserRequest
import org.example.dto.UpdateUserRequest
import org.example.dto.UserResponse
import org.example.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun findAll(): List<UserResponse> {
        TODO("Not yet implemented")
    }

    fun findById(id: Long): UserResponse {
        TODO("Not yet implemented")
    }

    fun create(request: CreateUserRequest): UserResponse {
        TODO("Not yet implemented")
    }

    fun update(id: Long, request: UpdateUserRequest): UserResponse {
        TODO("Not yet implemented")
    }

    fun deleteById(id: Long) {
        TODO("Not yet implemented")
    }
}

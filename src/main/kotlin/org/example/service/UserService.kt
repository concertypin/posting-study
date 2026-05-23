package org.example.service

import org.example.dto.CreateUserRequest
import org.example.dto.UpdateUserRequest
import org.example.dto.UserResponse
import org.example.model.User
import org.example.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun findAll(): List<UserResponse> {
        return userRepository.findAll().map { toResponse(it) }
    }

    fun findById(id: Long): UserResponse? {
        val user = userRepository.findById(id) ?: return null
        return toResponse(user)
    }

    fun create(request: CreateUserRequest): UserResponse {
        val user = User(
            id = 0L,
            username = request.username,
            nickname = request.nickname,
            createdAt = LocalDateTime.now()
        )
        return toResponse(userRepository.save(user))
    }

    fun update(id: Long, request: UpdateUserRequest): UserResponse? {
        val existing = userRepository.findById(id) ?: return null
        val updated = existing.copy(nickname = request.nickname)
        return toResponse(userRepository.save(updated))
    }

    fun deleteById(id: Long): Boolean {
        userRepository.findById(id) ?: return false
        return userRepository.deleteById(id)
    }

    private fun toResponse(user: User): UserResponse {
        return UserResponse(
            id = user.id,
            username = user.username,
            nickname = user.nickname,
            createdAt = user.createdAt
        )
    }
}
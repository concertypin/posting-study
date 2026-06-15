package org.example.service

import org.example.dto.CreateUserRequest
import org.example.dto.UpdateUserRequest
import org.example.dto.UserResponse
import org.example.model.User
import org.example.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun findAll(): List<UserResponse> {
        return userRepository.findAll().map { it.toResponse() }
    }

    fun findById(id: Long): UserResponse? {
        return userRepository.findById(id)?.toResponse()
    }

    fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    @Transactional
    fun create(request: CreateUserRequest): UserResponse {
        val user = User(
            id = 0L,
            username = request.username,
            password = passwordEncoder.encode(request.password),
            nickname = request.nickname,
            createdAt = LocalDateTime.now()
        )
        return userRepository.save(user).toResponse()
    }

    @Transactional
    fun update(id: Long, request: UpdateUserRequest): UserResponse? {
        val existing = userRepository.findById(id) ?: return null
        val updated = existing.copy(nickname = request.nickname)
        return userRepository.save(updated).toResponse()
    }

    @Transactional
    fun deleteById(id: Long): Boolean {
        userRepository.findById(id) ?: return false
        return userRepository.deleteById(id)
    }

    fun authenticate(username: String, password: String): UserResponse? {
        val user = userRepository.findByUsername(username) ?: return null
        if (!passwordEncoder.matches(password, user.password)) return null
        return user.toResponse()
    }

    private fun User.toResponse() = UserResponse(
        id = id, username = username, nickname = nickname, createdAt = createdAt
    )
}

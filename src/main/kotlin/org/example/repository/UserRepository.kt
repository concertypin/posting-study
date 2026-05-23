package org.example.repository

import org.example.model.User

interface UserRepository {

    fun findAll(): List<User>

    fun findById(id: Long): User?

    fun findByUsername(username: String): User?

    fun save(user: User): User

    fun deleteById(id: Long): Boolean
}

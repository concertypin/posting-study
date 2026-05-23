package org.example.repository

import org.example.model.User
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Repository
class InMemoryUserRepository : UserRepository {

    private val store = ConcurrentHashMap<Long, User>()
    private val sequence = AtomicLong(1)

    override fun findAll(): List<User> {
        TODO("Not yet implemented")
    }

    override fun findById(id: Long): User? {
        TODO("Not yet implemented")
    }

    override fun findByUsername(username: String): User? {
        TODO("Not yet implemented")
    }

    override fun save(user: User): User {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Long): Boolean {
        TODO("Not yet implemented")
    }
}

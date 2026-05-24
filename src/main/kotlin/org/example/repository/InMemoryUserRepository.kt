package org.example.repository

import org.example.model.User
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Repository
@Profile("!deploy")
class InMemoryUserRepository : UserRepository {

    private val store = ConcurrentHashMap<Long, User>()
    private val sequence = AtomicLong(1)

    override fun findAll(): List<User> = store.values.toList()

    override fun findById(id: Long): User? = store[id]

    override fun findByUsername(username: String): User? =
        store.values.find { it.username == username }

    override fun save(user: User): User {
        val id = user.id.takeIf { it != 0L } ?: sequence.getAndIncrement()
        val saved = user.copy(id = id)
        store[id] = saved
        return saved
    }

    override fun deleteById(id: Long): Boolean = store.remove(id) != null
}

package org.example.repository

import org.example.model.User
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

@Repository
@Profile("default")
class InMemoryUserRepository : UserRepository {

    private val store = ConcurrentHashMap<Long, User>()
    private val sequence = AtomicLong(1)
    private val lock = ReentrantReadWriteLock()

    override fun findAll(): List<User> {
        return lock.read { store.values.toList() }
    }

    override fun findById(id: Long): User? {
        return lock.read { store[id] }
    }

    override fun findByUsername(username: String): User? {
        return lock.read { store.values.find { it.username == username } }
    }

    override fun save(user: User): User {
        val id = user.id.takeIf { it != 0L } ?: sequence.getAndIncrement()
        val saved = user.copy(id = id)
        lock.write { store[id] = saved }
        return saved
    }

    override fun deleteById(id: Long): Boolean {
        return lock.write { store.remove(id) != null }
    }
}

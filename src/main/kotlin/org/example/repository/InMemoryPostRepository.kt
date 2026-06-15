package org.example.repository

import org.example.model.Post
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

@Repository
@Profile("default")
class InMemoryPostRepository : PostRepository {

    private val store = ConcurrentHashMap<Long, Post>()
    private val sequence = AtomicLong(1)
    private val lock = ReentrantReadWriteLock()

    override fun findAll(): List<Post> {
        return lock.read { store.values.toList() }
    }

    override fun findById(id: Long): Post? {
        return lock.read { store[id] }
    }

    override fun findByAuthorId(authorId: Long): List<Post> {
        return lock.read {
            store.values.filter { it.authorId == authorId }.sortedByDescending { it.createdAt }
        }
    }

    override fun save(post: Post): Post {
        val id = if (post.id == 0L) sequence.getAndIncrement() else post.id
        val saved = post.copy(id = id)
        lock.write { store[id] = saved }
        return saved
    }

    override fun deleteById(id: Long): Boolean {
        return lock.write { store.remove(id) != null }
    }

    override fun findByCursor(cursor: LocalDateTime?, limit: Int): List<Post> {
        return lock.read {
            store.values
                .sortedByDescending { it.createdAt }
                .filter { cursor == null || it.createdAt < cursor }
                .take(limit)
        }
    }
}


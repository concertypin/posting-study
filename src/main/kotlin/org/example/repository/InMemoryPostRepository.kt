package org.example.repository

import org.example.model.Post
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Repository
@Profile("!deploy")
class InMemoryPostRepository : PostRepository {

    private val store = ConcurrentHashMap<Long, Post>()
    private val sequence = AtomicLong(1)

    override fun findAll(): List<Post> {
        return store.values.toList()
    }

    override fun findById(id: Long): Post? {
        if (store.containsKey(id)) {
            return store[id]
        }
        return null
    }

    override fun save(post: Post): Post {
        val id = if (post.id == 0L) sequence.getAndIncrement() else post.id
        val saved = post.copy(id = id)
        store.put(id, saved)
        return saved
    }

    override fun deleteById(id: Long): Boolean {
        val removed = store.remove(id)
        return removed != null
    }
}

package org.example.repository

import org.example.model.Post
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Repository
class InMemoryPostRepository : PostRepository {

    private val store = ConcurrentHashMap<Long, Post>()
    private val sequence = AtomicLong(1)

    override fun findAll(): List<Post> {
        TODO("Not yet implemented")
    }

    override fun findById(id: Long): Post? {
        TODO("Not yet implemented")
    }

    override fun save(post: Post): Post {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Long): Boolean {
        TODO("Not yet implemented")
    }
}

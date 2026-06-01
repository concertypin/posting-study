package org.example.repository

import org.example.model.Post
import java.time.LocalDateTime

interface PostRepository {

    fun findAll(): List<Post>

    fun findById(id: Long): Post?

    fun findByAuthorId(authorId: Long): List<Post>

    fun save(post: Post): Post

    fun deleteById(id: Long): Boolean

    fun findByCursor(cursor: LocalDateTime?, limit: Int): List<Post> {
        throw UnsupportedOperationException("Cursor-based pagination not implemented")
    }
}

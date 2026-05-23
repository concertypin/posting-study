package org.example.repository

import org.example.model.Post

interface PostRepository {

    fun findAll(): List<Post>

    fun findById(id: Long): Post?

    fun save(post: Post): Post

    fun deleteById(id: Long): Boolean
}

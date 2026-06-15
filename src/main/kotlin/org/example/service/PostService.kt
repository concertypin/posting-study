package org.example.service

import org.example.dto.CreatePostRequest
import org.example.dto.UpdatePostRequest
import org.example.dto.PostResponse
import org.example.dto.CursorPageResponse
import org.example.model.Post
import org.example.repository.PostRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PostService(
    private val postRepository: PostRepository
) {

    companion object {
        private const val MAX_LIMIT = 100
    }

    fun findAll(): List<PostResponse> {
        return postRepository.findAll().map { post ->
            PostResponse(
                id = post.id,
                title = post.title,
                content = post.content,
                authorId = post.authorId,
                createdAt = post.createdAt,
                updatedAt = post.updatedAt
            )
        }
    }

    fun findById(id: Long): PostResponse {
        val post = postRepository.findById(id) ?: throw NoSuchElementException("Post not found: $id")
        return PostResponse(
            id = post.id,
            title = post.title,
            content = post.content,
            authorId = post.authorId,
            createdAt = post.createdAt,
            updatedAt = post.updatedAt
        )
    }

    fun create(request: CreatePostRequest): PostResponse {
        val post = Post(
            id = 0L,
            title = request.title,
            content = request.content,
            authorId = request.authorId,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val saved = postRepository.save(post)
        return PostResponse(
            id = saved.id,
            title = saved.title,
            content = saved.content,
            authorId = saved.authorId,
            createdAt = saved.createdAt,
            updatedAt = saved.updatedAt
        )
    }

    fun update(id: Long, request: UpdatePostRequest): PostResponse {
        val existing = postRepository.findById(id) ?: throw NoSuchElementException("Post not found: $id")
        val updated = existing.copy(
            title = request.title,
            content = request.content,
            updatedAt = LocalDateTime.now()
        )
        val saved = postRepository.save(updated)
        return PostResponse(
            id = saved.id,
            title = saved.title,
            content = saved.content,
            authorId = saved.authorId,
            createdAt = saved.createdAt,
            updatedAt = saved.updatedAt
        )
    }

    fun deleteById(id: Long) {
        postRepository.findById(id) ?: throw NoSuchElementException("Post not found: $id")
        postRepository.deleteById(id)
    }

    fun findByCursor(cursor: LocalDateTime?, limit: Int): CursorPageResponse<PostResponse> {
        val safeLimit = limit.coerceIn(1, MAX_LIMIT)
        val posts = postRepository.findByCursor(cursor, safeLimit + 1)

        val hasMore = posts.size > safeLimit
        val pagePosts = if (hasMore) posts.dropLast(1) else posts
        val nextCursor = if (hasMore) pagePosts.last().createdAt else null

        val data = pagePosts.map { post ->
            PostResponse(
                id = post.id,
                title = post.title,
                content = post.content,
                authorId = post.authorId,
                createdAt = post.createdAt,
                updatedAt = post.updatedAt
            )
        }

        return CursorPageResponse(data = data, nextCursor = nextCursor)
    }
}

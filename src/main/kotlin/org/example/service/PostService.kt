package org.example.service

import org.example.dto.CreatePostRequest
import org.example.dto.UpdatePostRequest
import org.example.dto.PostResponse
import org.example.dto.CursorPageResponse
import org.example.model.Post
import org.example.repository.PostRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class PostService(
    private val postRepository: PostRepository
) {

    companion object {
        private const val MAX_LIMIT = 100
    }

    fun findAll(): List<PostResponse> {
        return postRepository.findAll().map { it.toResponse() }
    }

    fun findById(id: Long): PostResponse {
        val post = postRepository.findById(id) ?: throw NoSuchElementException("Post not found: $id")
        return post.toResponse()
    }

    @Transactional
    fun create(request: CreatePostRequest): PostResponse {
        val post = Post(
            id = 0L,
            title = request.title,
            content = request.content,
            authorId = request.authorId,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        return postRepository.save(post).toResponse()
    }

    @Transactional
    fun update(id: Long, request: UpdatePostRequest): PostResponse {
        val existing = postRepository.findById(id) ?: throw NoSuchElementException("Post not found: $id")
        val updated = existing.copy(
            title = request.title,
            content = request.content,
            updatedAt = LocalDateTime.now()
        )
        return postRepository.save(updated).toResponse()
    }

    @Transactional
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

        val data = pagePosts.map { it.toResponse() }
        return CursorPageResponse(data = data, nextCursor = nextCursor)
    }

    private fun Post.toResponse() = PostResponse(
        id = id, title = title, content = content, authorId = authorId,
        createdAt = createdAt, updatedAt = updatedAt
    )
}

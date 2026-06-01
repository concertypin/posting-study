package org.example.controller

import org.example.dto.CreatePostRequest
import org.example.dto.UpdatePostRequest
import org.example.dto.PostResponse
import org.example.dto.CursorPageResponse
import org.example.service.PostService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/posts")
class PostController(
    private val postService: PostService
) {

    @GetMapping
    fun list(
        @RequestParam(required = false) cursor: LocalDateTime?,
        @RequestParam(defaultValue = "20") limit: Int
    ): CursorPageResponse<PostResponse> {
        return postService.findByCursor(cursor, limit)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<PostResponse> {
        return try {
            ResponseEntity.ok(postService.findById(id))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun create(@RequestBody request: CreatePostRequest): PostResponse {
        return postService.create(request)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: UpdatePostRequest): ResponseEntity<PostResponse> {
        return try {
            ResponseEntity.ok(postService.update(id, request))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        return try {
            postService.deleteById(id)
            ResponseEntity.noContent().build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }
}

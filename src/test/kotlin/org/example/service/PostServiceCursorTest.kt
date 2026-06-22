package org.example.service

import org.example.model.Post
import org.example.repository.PostRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PostServiceCursorTest {

    private lateinit var service: PostService
    private lateinit var repository: TestPostRepository

    @BeforeEach
    fun setUp() {
        repository = TestPostRepository()
        service = PostService(repository)
    }

    @Test
    fun `Service sets nextCursor correctly when more pages exist`() {
        val t1 = LocalDateTime.of(2026, 6, 1, 10, 0, 0)
        val t2 = t1.plusMinutes(1)
        val t3 = t1.plusMinutes(2)
        val t4 = t1.plusMinutes(3)
        val t5 = t1.plusMinutes(4)

        repository.save(Post(0L, "t1", "c", 1L, t1, t1))
        repository.save(Post(0L, "t2", "c", 1L, t2, t2))
        repository.save(Post(0L, "t3", "c", 1L, t3, t3))
        repository.save(Post(0L, "t4", "c", 1L, t4, t4))
        repository.save(Post(0L, "t5", "c", 1L, t5, t5))

        val result = service.findByCursor(null, 2)

        assertEquals(2, result.data.size)
        assertNotNull(result.nextCursor)
        assertEquals(t5, result.data[0].createdAt)
        assertEquals(t4, result.data[1].createdAt)
        assertEquals(t4, result.nextCursor)
    }

    @Test
    fun `Service sets nextCursor null when last page`() {
        val t1 = LocalDateTime.of(2026, 6, 1, 10, 0, 0)
        val t2 = t1.plusMinutes(1)
        val t3 = t1.plusMinutes(2)

        repository.save(Post(0L, "t1", "c", 1L, t1, t1))
        repository.save(Post(0L, "t2", "c", 1L, t2, t2))
        repository.save(Post(0L, "t3", "c", 1L, t3, t3))

        val result = service.findByCursor(null, 5)

        assertEquals(3, result.data.size)
        assertNull(result.nextCursor)
    }

    @Test
    fun `Service caps limit to max 100`() {
        val t1 = LocalDateTime.of(2026, 6, 1, 10, 0, 0)
        for (i in 1..150) {
            repository.save(Post(0L, "t$i", "c", 1L, t1.plusSeconds(i.toLong()), t1.plusSeconds(i.toLong())))
        }

        val result = service.findByCursor(null, 999)

        assertEquals(100, result.data.size)
        assertNotNull(result.nextCursor)
    }

    private class TestPostRepository : PostRepository {
        private val store = mutableMapOf<Long, Post>()
        private var seq = 1L

        override fun findAll(): List<Post> = store.values.toList()
        override fun findById(id: Long): Post? = store[id]
        override fun findByAuthorId(authorId: Long): List<Post> = store.values.filter { it.authorId == authorId }
        override fun save(post: Post): Post {
            val id = if (post.id == 0L) seq++ else post.id
            val saved = Post(id, post.title, post.content, post.authorId, post.createdAt, post.updatedAt)
            store[id] = saved
            return saved
        }
        override fun deleteById(id: Long): Boolean = store.remove(id) != null

        override fun findByCursor(cursor: LocalDateTime?, limit: Int): List<Post> {
            return store.values
                .sortedByDescending { it.createdAt }
                .filter { cursor == null || it.createdAt < cursor }
                .take(limit)
        }
    }
}

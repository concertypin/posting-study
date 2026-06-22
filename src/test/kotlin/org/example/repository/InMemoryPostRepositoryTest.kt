package org.example.repository

import org.example.model.Post
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InMemoryPostRepositoryTest {

    private lateinit var repository: InMemoryPostRepository

    @BeforeEach
    fun setUp() {
        repository = InMemoryPostRepository()
    }

    private fun createPost(id: Long, createdAt: LocalDateTime): Post {
        return Post(0L, "title$id", "content$id", 1L, createdAt, createdAt)
    }

    @Test
    fun `findByCursor with null cursor returns first N posts`() {
        val t1 = LocalDateTime.of(2026, 6, 1, 10, 0, 0)
        val t2 = t1.plusMinutes(1)
        val t3 = t1.plusMinutes(2)
        val t4 = t1.plusMinutes(3)
        val t5 = t1.plusMinutes(4)

        repository.save(createPost(0L, t1))
        repository.save(createPost(0L, t2))
        repository.save(createPost(0L, t3))
        repository.save(createPost(0L, t4))
        repository.save(createPost(0L, t5))

        val result = repository.findByCursor(null, 3)

        assertEquals(3, result.size)
        assertEquals(t5, result[0].createdAt)
        assertEquals(t4, result[1].createdAt)
        assertEquals(t3, result[2].createdAt)
    }

    @Test
    fun `findByCursor with cursor returns older posts`() {
        val t1 = LocalDateTime.of(2026, 6, 1, 10, 0, 0)
        val t2 = t1.plusMinutes(1)
        val t3 = t1.plusMinutes(2)
        val t4 = t1.plusMinutes(3)
        val t5 = t1.plusMinutes(4)

        repository.save(createPost(0L, t1))
        repository.save(createPost(0L, t2))
        repository.save(createPost(0L, t3))
        repository.save(createPost(0L, t4))
        repository.save(createPost(0L, t5))

        val result = repository.findByCursor(t4, 3)

        assertEquals(3, result.size)
        assertEquals(t3, result[0].createdAt)
        assertEquals(t2, result[1].createdAt)
        assertEquals(t1, result[2].createdAt)
    }

    @Test
    fun `findByCursor with cursor past the end returns empty list`() {
        val t1 = LocalDateTime.of(2026, 6, 1, 10, 0, 0)
        val t2 = t1.plusMinutes(1)

        repository.save(createPost(0L, t1))
        repository.save(createPost(0L, t2))

        val result = repository.findByCursor(t1.minusDays(1), 10)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `findByCursor with limit greater than total returns all`() {
        val t1 = LocalDateTime.of(2026, 6, 1, 10, 0, 0)
        val t2 = t1.plusMinutes(1)
        val t3 = t1.plusMinutes(2)

        repository.save(createPost(0L, t1))
        repository.save(createPost(0L, t2))
        repository.save(createPost(0L, t3))

        val result = repository.findByCursor(null, 10)

        assertEquals(3, result.size)
    }
}

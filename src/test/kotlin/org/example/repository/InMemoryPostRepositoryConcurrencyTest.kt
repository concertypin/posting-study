package org.example.repository

import org.example.model.Post
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InMemoryPostRepositoryConcurrencyTest {

    @Test
    fun `concurrent saves produce unique ids`() {
        val repository = InMemoryPostRepository()
        val threadCount = 10
        val savesPerThread = 100
        val executor = Executors.newFixedThreadPool(threadCount)
        val allIds = ConcurrentSkipListSet<Long>()
        val latch = CountDownLatch(threadCount)

        repeat(threadCount) {
            executor.submit {
                try {
                    repeat(savesPerThread) {
                        val post = repository.save(
                            Post(
                                id = 0L,
                                title = "title",
                                content = "content",
                                authorId = 1L,
                                createdAt = LocalDateTime.now(),
                                updatedAt = LocalDateTime.now()
                            )
                        )
                        assertTrue(allIds.add(post.id), "Duplicate ID: ${post.id}")
                    }
                } finally {
                    latch.countDown()
                }
            }
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS), "Timed out waiting for saves")
        executor.shutdown()

        assertEquals(threadCount * savesPerThread, repository.findAll().size)
    }

    @Test
    fun `concurrent reads tolerate concurrent writes`() {
        val repository = InMemoryPostRepository()
        val executor = Executors.newFixedThreadPool(4)
        val latch = CountDownLatch(2)

        // Writer thread
        executor.submit {
            try {
                repeat(1000) {
                    val post = Post(
                        id = 0L,
                        title = "t$it",
                        content = "c$it",
                        authorId = 1L,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                    repository.save(post)
                    if (it % 2 == 0) {
                        repository.deleteById(it.toLong())
                    }
                }
            } finally {
                latch.countDown()
            }
        }

        // Reader thread
        executor.submit {
            try {
                repeat(1000) {
                    // Should never throw ConcurrentModificationException or similar
                    repository.findAll()
                    repository.findByAuthorId(1L)
                    repository.findByCursor(null, 10)
                }
            } finally {
                latch.countDown()
            }
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS), "Timed out")
        executor.shutdown()
    }
}

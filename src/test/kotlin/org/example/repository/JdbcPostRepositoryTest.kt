package org.example.repository

import org.example.model.Post
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(properties = [
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.autoconfigure.exclude="
])
@ActiveProfiles("production")
class JdbcPostRepositoryTest {

    @Autowired
    private lateinit var repository: JdbcPostRepository

    @Autowired
    private lateinit var jdbc: JdbcTemplate

    @BeforeEach
    fun setUp() {
        jdbc.update("DELETE FROM posts")
        jdbc.update("DELETE FROM users")
        jdbc.update(
            "INSERT INTO users (id, username, password, nickname, created_at) VALUES (?, ?, ?, ?, ?)",
            1L, "author1", "pass", "Author1",
            java.sql.Timestamp.valueOf(LocalDateTime.of(2026, 6, 8, 0, 0, 0))
        )
    }

    @Test
    fun `save inserts and returns generated key`() {
        val saved = repository.save(Post(0L, "Title", "Content", 1L, LocalDateTime.now(), LocalDateTime.now()))

        assertTrue(saved.id > 0)
        assertEquals("Title", saved.title)
    }

    @Test
    fun `findById returns post when exists`() {
        jdbc.update(
            "INSERT INTO posts (id, title, content, author_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)",
            100L, "Existing", "Content", 1L,
            java.sql.Timestamp.valueOf(LocalDateTime.of(2026, 6, 8, 0, 0, 0)),
            java.sql.Timestamp.valueOf(LocalDateTime.of(2026, 6, 8, 0, 0, 0))
        )
        assertNotNull(repository.findById(100L))
    }

    @Test
    fun `findById returns null when not exists`() {
        assertNull(repository.findById(999L))
    }

    @Test
    fun `findByCursor returns latest first`() {
        val t1 = LocalDateTime.of(2026, 6, 8, 10, 0, 0)
        repository.save(Post(0L, "p1", "c", 1L, t1, t1))
        repository.save(Post(0L, "p2", "c", 1L, t1.plusMinutes(1), t1.plusMinutes(1)))
        repository.save(Post(0L, "p3", "c", 1L, t1.plusMinutes(2), t1.plusMinutes(2)))

        val result = repository.findByCursor(null, 2)
        assertEquals(2, result.size)
        assertEquals("p3", result[0].title)
    }

    @Test
    fun `findByCursor paginates correctly`() {
        val t1 = LocalDateTime.of(2026, 6, 8, 10, 0, 0)
        repeat(5) {
            repository.save(Post(0L, "p${it + 1}", "c", 1L, t1.plusMinutes(it.toLong()), t1.plusMinutes(it.toLong())))
        }

        val page1 = repository.findByCursor(null, 2)
        assertEquals(2, page1.size)

        val page2 = repository.findByCursor(page1.last().createdAt, 2)
        assertEquals(2, page2.size)
        assertTrue(page2[0].createdAt < page1.last().createdAt)
    }

    @Test
    fun `deleteById removes post`() {
        jdbc.update(
            "INSERT INTO posts (id, title, content, author_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)",
            200L, "ToDelete", "Content", 1L,
            java.sql.Timestamp.valueOf(LocalDateTime.now()),
            java.sql.Timestamp.valueOf(LocalDateTime.now())
        )
        assertTrue(repository.deleteById(200L))
        assertNull(repository.findById(200L))
    }
}

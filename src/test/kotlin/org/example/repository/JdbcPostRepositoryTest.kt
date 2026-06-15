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
    "spring.autoconfigure.exclude=",
    "spring.flyway.enabled=true",
    "spring.flyway.locations=classpath:db/testmigration"
])
@ActiveProfiles("test", "production")
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
            "INSERT INTO users (id, username, password, nickname, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)",
            1L, "author1", "pass", "Author1",
            java.sql.Timestamp.valueOf(LocalDateTime.of(2026, 6, 8, 0, 0, 0)),
            java.sql.Timestamp.valueOf(LocalDateTime.of(2026, 6, 8, 0, 0, 0))
        )
    }

    @Test
    fun `save inserts and returns generated key and timestamps`() {
        val now = LocalDateTime.of(2026, 6, 8, 10, 0, 0)
        val post = Post(0L, "Title", "Content", 1L, now, now)

        val saved = repository.save(post)

        assertNotNull(saved)
        assertTrue(saved.id > 0)
        assertEquals("Title", saved.title)
        assertEquals("Content", saved.content)
        assertEquals(1L, saved.authorId)
        assertNotNull(saved.createdAt)
        assertNotNull(saved.updatedAt)
    }

    @Test
    fun `findById returns post when exists`() {
        jdbc.update(
            "INSERT INTO posts (id, title, content, author_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)",
            100L, "Existing", "Content", 1L,
            java.sql.Timestamp.valueOf(LocalDateTime.of(2026, 6, 8, 0, 0, 0)),
            java.sql.Timestamp.valueOf(LocalDateTime.of(2026, 6, 8, 0, 0, 0))
        )

        val found = repository.findById(100L)
        assertNotNull(found)
        assertEquals("Existing", found.title)
    }

    @Test
    fun `findById returns null when not exists`() {
        val found = repository.findById(999L)
        assertNull(found)
    }

    @Test
    fun `findByCursor with null cursor returns latest N posts`() {
        val t1 = LocalDateTime.of(2026, 6, 8, 10, 0, 0)
        val t2 = t1.plusMinutes(1)
        val t3 = t1.plusMinutes(2)
        val t4 = t1.plusMinutes(3)
        val t5 = t1.plusMinutes(4)

        repository.save(Post(0L, "p1", "c1", 1L, t1, t1))
        repository.save(Post(0L, "p2", "c2", 1L, t2, t2))
        repository.save(Post(0L, "p3", "c3", 1L, t3, t3))
        repository.save(Post(0L, "p4", "c4", 1L, t4, t4))
        repository.save(Post(0L, "p5", "c5", 1L, t5, t5))

        val result = repository.findByCursor(null, 3)

        assertEquals(3, result.size)
        assertEquals("p5", result[0].title)
        assertEquals("p4", result[1].title)
        assertEquals("p3", result[2].title)
    }

    @Test
    fun `findByCursor with cursor returns older posts`() {
        val t1 = LocalDateTime.of(2026, 6, 8, 10, 0, 0)
        val t2 = t1.plusMinutes(1)
        val t3 = t1.plusMinutes(2)
        val t4 = t1.plusMinutes(3)
        val t5 = t1.plusMinutes(4)

        repository.save(Post(0L, "p1", "c1", 1L, t1, t1))
        repository.save(Post(0L, "p2", "c2", 1L, t2, t2))
        repository.save(Post(0L, "p3", "c3", 1L, t3, t3))
        repository.save(Post(0L, "p4", "c4", 1L, t4, t4))
        repository.save(Post(0L, "p5", "c5", 1L, t5, t5))

        val result = repository.findByCursor(t4, 3)

        assertEquals(3, result.size)
        assertEquals("p3", result[0].title)
        assertEquals("p2", result[1].title)
        assertEquals("p1", result[2].title)
    }

    @Test
    fun `findByCursor with cursor past all posts returns empty`() {
        val t1 = LocalDateTime.of(2026, 6, 8, 10, 0, 0)
        repository.save(Post(0L, "p1", "c1", 1L, t1, t1))

        val result = repository.findByCursor(t1.minusDays(1), 10)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `findByCursor with limit greater than total returns all`() {
        val t1 = LocalDateTime.of(2026, 6, 8, 10, 0, 0)
        val t2 = t1.plusMinutes(1)
        val t3 = t1.plusMinutes(2)

        repository.save(Post(0L, "p1", "c1", 1L, t1, t1))
        repository.save(Post(0L, "p2", "c2", 1L, t2, t2))
        repository.save(Post(0L, "p3", "c3", 1L, t3, t3))

        val result = repository.findByCursor(null, 10)
        assertEquals(3, result.size)
    }

    @Test
    fun `deleteById removes post and findById returns null`() {
        jdbc.update(
            "INSERT INTO posts (id, title, content, author_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)",
            200L, "ToDelete", "Content", 1L,
            java.sql.Timestamp.valueOf(LocalDateTime.of(2026, 6, 8, 0, 0, 0)),
            java.sql.Timestamp.valueOf(LocalDateTime.of(2026, 6, 8, 0, 0, 0))
        )

        assertNotNull(repository.findById(200L))

        val deleted = repository.deleteById(200L)
        assertTrue(deleted)
        assertNull(repository.findById(200L))
    }
}

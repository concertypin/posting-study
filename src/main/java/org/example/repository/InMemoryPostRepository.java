package org.example.repository;

import org.example.model.Post;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Repository
@Profile("default")
public class InMemoryPostRepository implements PostRepository {

    private final ConcurrentHashMap<Long, Post> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    @Override
    public List<Post> findAll() {
        rwLock.readLock().lock();
        try {
            return new ArrayList<>(store.values());
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public Post findById(Long id) {
        rwLock.readLock().lock();
        try {
            return store.get(id);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public List<Post> findByAuthorId(Long authorId) {
        rwLock.readLock().lock();
        try {
            return store.values().stream()
                .filter(p -> p.getAuthorId().equals(authorId))
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .collect(Collectors.toList());
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public Post save(Post post) {
        Long id = post.getId();
        if (id == null || id == 0L) {
            id = sequence.getAndIncrement();
        }
        Post saved = new Post(id, post.getTitle(), post.getContent(),
            post.getAuthorId(), post.getCreatedAt(), post.getUpdatedAt());

        rwLock.writeLock().lock();
        try {
            store.put(id, saved);
        } finally {
            rwLock.writeLock().unlock();
        }
        return saved;
    }

    @Override
    public boolean deleteById(Long id) {
        rwLock.writeLock().lock();
        try {
            return store.remove(id) != null;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public List<Post> findByCursor(LocalDateTime cursor, int limit) {
        rwLock.readLock().lock();
        try {
            return store.values().stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .filter(p -> cursor == null || p.getCreatedAt().isBefore(cursor))
                .limit(limit)
                .collect(Collectors.toList());
        } finally {
            rwLock.readLock().unlock();
        }
    }
}

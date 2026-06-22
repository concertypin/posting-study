package org.example.repository;

import org.example.model.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
@Profile("default")
public class InMemoryUserRepository implements UserRepository {

    private final ConcurrentHashMap<Long, User> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public List<User> findAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(store.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public User findById(Long id) {
        lock.readLock().lock();
        try {
            return store.get(id);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public User findByUsername(String username) {
        lock.readLock().lock();
        try {
            for (User u : store.values()) {
                if (u.getUsername().equals(username)) return u;
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public User save(User user) {
        Long id = user.getId();
        if (id == null || id == 0L) {
            id = sequence.getAndIncrement();
        }
        User saved = new User(id, user.getUsername(), user.getPassword(),
            user.getNickname(), user.getCreatedAt());

        lock.writeLock().lock();
        try {
            store.put(id, saved);
        } finally {
            lock.writeLock().unlock();
        }
        return saved;
    }

    @Override
    public boolean deleteById(Long id) {
        lock.writeLock().lock();
        try {
            return store.remove(id) != null;
        } finally {
            lock.writeLock().unlock();
        }
    }
}

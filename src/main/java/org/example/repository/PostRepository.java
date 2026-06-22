package org.example.repository;

import org.example.model.Post;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository {

    List<Post> findAll();

    Post findById(Long id);

    List<Post> findByAuthorId(Long authorId);

    Post save(Post post);

    boolean deleteById(Long id);

    // TODO: 커서 기반 페이지네이션 - 구현체에서 Override 안하면 예외
    default List<Post> findByCursor(LocalDateTime cursor, int limit) {
        throw new UnsupportedOperationException("Cursor-based pagination not implemented");
    }
}

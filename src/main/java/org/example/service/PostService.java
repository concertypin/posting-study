package org.example.service;

import org.example.dto.CreatePostRequest;
import org.example.dto.CursorPageResponse;
import org.example.dto.PostResponse;
import org.example.dto.UpdatePostRequest;
import org.example.model.Post;
import org.example.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    // 상수
    private static final int MAX_LIMIT = 100;

    // 생성자 주입
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * 전체 게시글 조회
     */
    public List<PostResponse> findAll() {
        return postRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public PostResponse findById(Long id) {
        Post post = postRepository.findById(id);
        if (post == null) {
            throw new NoSuchElementException("Post not found: " + id);
        }
        return toResponse(post);
    }

    @Transactional
    public PostResponse create(CreatePostRequest request) {
        // 새 Post 객체 생성
        Post post = new Post(
            0L,
            request.getTitle(),
            request.getContent(),
            request.getAuthorId(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        Post saved = postRepository.save(post);
        return toResponse(saved);
    }

    @Transactional
    public PostResponse update(Long id, UpdatePostRequest request) {
        Post existing = postRepository.findById(id);
        if (existing == null) {
            throw new NoSuchElementException("Post not found: " + id);
        }

        // 내용 업데이트 (불변객체가 아니라 setter 사용)
        existing.setTitle(request.getTitle());
        existing.setContent(request.getContent());
        existing.setUpdatedAt(LocalDateTime.now());

        Post updated = postRepository.save(existing);
        return toResponse(updated);
    }

    @Transactional
    public void deleteById(Long id) {
        Post post = postRepository.findById(id);
        if (post == null) {
            throw new NoSuchElementException("Post not found: " + id);
        }
        postRepository.deleteById(id);
    }

    // 커서 기반 페이지네이션
    public CursorPageResponse<PostResponse> findByCursor(LocalDateTime cursor, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, MAX_LIMIT));

        // limit + 1 개를 가져와서 다음 페이지 있는지 확인
        List<Post> posts = postRepository.findByCursor(cursor, safeLimit + 1);

        boolean hasMore = posts.size() > safeLimit;
        List<Post> pagePosts = hasMore ? posts.subList(0, safeLimit) : posts;

        LocalDateTime nextCursor = hasMore
            ? pagePosts.get(pagePosts.size() - 1).getCreatedAt()
            : null;

        List<PostResponse> data = pagePosts.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());

        return new CursorPageResponse<>(data, nextCursor);
    }

    // Post -> PostResponse 변환
    private PostResponse toResponse(Post post) {
        return new PostResponse(
            post.getId(), post.getTitle(), post.getContent(),
            post.getAuthorId(), post.getCreatedAt(), post.getUpdatedAt()
        );
    }
}

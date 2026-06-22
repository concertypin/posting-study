package org.example.controller;

import org.example.dto.CreatePostRequest;
import org.example.dto.CursorPageResponse;
import org.example.dto.PostResponse;
import org.example.dto.UpdatePostRequest;
import org.example.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postSvc;

    public PostController(PostService postSvc) {
        this.postSvc = postSvc;
    }

    // 전체 목록 (커서 기반 페이지네이션)
    @GetMapping
    public CursorPageResponse<PostResponse> list(
            @RequestParam(required = false) LocalDateTime cursor,
            @RequestParam(defaultValue = "20") int limit) {
        return postSvc.findByCursor(cursor, limit);
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> get(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(postSvc.findById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 생성
    @PostMapping
    public PostResponse create(@RequestBody CreatePostRequest request) {
        return postSvc.create(request);
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> update(
            @PathVariable Long id,
            @RequestBody UpdatePostRequest request) {
        try {
            return ResponseEntity.ok(postSvc.update(id, request));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            postSvc.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

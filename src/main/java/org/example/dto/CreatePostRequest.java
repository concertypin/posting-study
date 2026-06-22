package org.example.dto;

// 게시글 생성 요청 DTO
public class CreatePostRequest {

    private String title;
    private String content;
    private Long authorId;

    public CreatePostRequest() {}

    public CreatePostRequest(String title, String content, Long authorId) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
}

package org.example.dto;

import java.time.LocalDateTime;
import java.util.List;

// 제네릭 커서 페이지 응답 - nextCursor가 null이면 마지막 페이지
public class CursorPageResponse<T> {

    private List<T> data;
    private LocalDateTime nextCursor; // null = no more pages

    public CursorPageResponse() {}

    public CursorPageResponse(List<T> data, LocalDateTime nextCursor) {
        this.data = data;
        this.nextCursor = nextCursor;
    }

    public List<T> getData() { return data; }
    public void setData(List<T> data) { this.data = data; }

    public LocalDateTime getNextCursor() { return nextCursor; }
    public void setNextCursor(LocalDateTime nextCursor) { this.nextCursor = nextCursor; }
}

package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Operation(summary = "API 정보", description = "Posting 애플리케이션의 API 정보를 반환합니다.")
    @GetMapping(value = "/info", produces = "text/markdown")
    public ResponseEntity<String> info() {
        String markdown = """
# Posting API

Spring Boot + Java 기반 게시판 애플리케이션입니다.

## 현재 제공 API

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | / | 홈 페이지 |
| GET | /login | 로그인 페이지 |
| GET | /api/info | 이 API 정보 |

---

*OpenAPI 문서: /swagger-ui.html*
        """.stripIndent();
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("text/markdown"))
            .body(markdown);
    }
}
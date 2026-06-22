package org.example.controller;

import jakarta.servlet.http.HttpSession;
import org.example.dto.CreatePostRequest;
import org.example.dto.UserResponse;
import org.example.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

// 게시판 화면 컨트롤러 (Thymeleaf 템플릿)
@Controller
@RequestMapping("/posts")
public class PostPageController {

    private final PostService postSvc;

    public PostPageController(PostService postSvc) {
        this.postSvc = postSvc;
    }

    // 게시글 목록
    @GetMapping
    public String list(Model model) {
        model.addAttribute("posts", postSvc.findAll());
        return "posts/list";
    }

    // 글쓰기 폼
    @GetMapping("/create")
    public String createForm(HttpSession session) {
        // 로그인 체크
        UserResponse user = (UserResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        return "posts/create";
    }

    // 글쓰기 처리
    @PostMapping("/create")
    public String create(@RequestParam String title,
                         @RequestParam String content,
                         HttpSession session) {
        UserResponse user = (UserResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        CreatePostRequest req = new CreatePostRequest(title, content, user.getId());
        postSvc.create(req);
        return "redirect:/posts";
    }

    // 게시글 상세 보기
    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("post", postSvc.findById(id));
            return "posts/view";
        } catch (NoSuchElementException e) {
            // 없는 게시글은 목록으로
            return "redirect:/posts";
        }
    }
}

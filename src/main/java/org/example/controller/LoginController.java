package org.example.controller;

import jakarta.servlet.http.HttpSession;
import org.example.dto.CreateUserRequest;
import org.example.dto.UserResponse;
import org.example.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final UserService userSvc; // 약어

    public LoginController(UserService userSvc) {
        this.userSvc = userSvc;
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String nickname) {
        // 회원가입 처리
        CreateUserRequest req = new CreateUserRequest(username, password, nickname);
        userSvc.create(req);
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {
        UserResponse user = userSvc.authenticate(username, password);
        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/";
        }
        // 로그인 실패
        return "redirect:/login?error";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

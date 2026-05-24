package org.example.controller

import jakarta.servlet.http.HttpSession
import org.example.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class LoginController(
    private val userService: UserService
) {

    @PostMapping("/login")
    fun login(
        @RequestParam username: String,
        @RequestParam password: String,
        session: HttpSession
    ): String {
        val user = userService.authenticate(username, password)
        if (user != null) {
            session.setAttribute("user", user)
            return "redirect:/"
        }
        return "redirect:/login?error"
    }

    @PostMapping("/logout")
    fun logout(session: HttpSession): String {
        session.invalidate()
        return "redirect:/login"
    }
}

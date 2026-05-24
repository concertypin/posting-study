package org.example.controller

import jakarta.servlet.http.HttpSession
import org.example.dto.CreatePostRequest
import org.example.dto.UserResponse
import org.example.service.PostService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/posts")
class PostPageController(
    private val postService: PostService
) {

    @GetMapping
    fun list(model: Model): String {
        model.addAttribute("posts", postService.findAll())
        return "posts/list"
    }

    @GetMapping("/create")
    fun createForm(session: HttpSession): String {
        val user = session.getAttribute("user") as? UserResponse ?: return "redirect:/login"
        return "posts/create"
    }

    @PostMapping("/create")
    fun create(
        @RequestParam title: String,
        @RequestParam content: String,
        session: HttpSession
    ): String {
        val user = session.getAttribute("user") as? UserResponse ?: return "redirect:/login"
        val request = CreatePostRequest(title = title, content = content, authorId = user.id)
        postService.create(request)
        return "redirect:/posts"
    }

    @GetMapping("/{id}")
    fun view(@PathVariable id: Long, model: Model): String {
        try {
            model.addAttribute("post", postService.findById(id))
            return "posts/view"
        } catch (e: NoSuchElementException) {
            return "redirect:/posts"
        }
    }
}

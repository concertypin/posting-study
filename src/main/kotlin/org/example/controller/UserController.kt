package org.example.controller

import org.example.dto.CreateUserRequest
import org.example.dto.UpdateUserRequest
import org.example.dto.UserResponse
import org.example.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping
    fun list(): List<UserResponse> = userService.findAll()

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<UserResponse> {
        val user = userService.findById(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(user)
    }

    @PostMapping
    fun create(@RequestBody request: CreateUserRequest): UserResponse {
        return userService.create(request)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: UpdateUserRequest): ResponseEntity<UserResponse> {
        val updated = userService.update(id, request) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        return if (userService.deleteById(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
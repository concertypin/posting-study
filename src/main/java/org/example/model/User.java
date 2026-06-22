package org.example.model;

import java.time.LocalDateTime;

public class User {

    private Long id;
    private String username;
    private String password;
    private String nickname;
    private LocalDateTime createdAt;

    public User() {}

    public User(Long id, String username, String password, String nickname,
                LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "'}";
    }
}

package org.example.dto;

public class UpdateUserRequest {

    private String nickname;

    public UpdateUserRequest() {}

    public UpdateUserRequest(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}

package org.example.service;

import org.example.dto.CreateUserRequest;
import org.example.dto.UpdateUserRequest;
import org.example.dto.UserResponse;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepo;  // 약어 사용
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> findAll() {
        return userRepo.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public UserResponse findById(Long id) {
        User user = userRepo.findById(id);
        return user != null ? toResponse(user) : null;
    }

    // TODO: 이거 로그인할 때 쓰는데 나중에 리팩토링
    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        User user = new User(
            0L,
            request.getUsername(),
            passwordEncoder.encode(request.getPassword()), // 비밀번호 암호화
            request.getNickname(),
            LocalDateTime.now()
        );
        User saved = userRepo.save(user);
        return toResponse(saved);
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User existing = userRepo.findById(id);
        if (existing == null) return null;

        existing.setNickname(request.getNickname());
        User updated = userRepo.save(existing);
        return toResponse(updated);
    }

    @Transactional
    public boolean deleteById(Long id) {
        User user = userRepo.findById(id);
        if (user == null) return false;
        return userRepo.deleteById(id);
    }

    // 인증 - username + password 검증
    public UserResponse authenticate(String username, String password) {
        User user = userRepo.findByUsername(username);
        if (user == null) return null;
        if (!passwordEncoder.matches(password, user.getPassword())) return null;
        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(), user.getUsername(),
            user.getNickname(), user.getCreatedAt()
        );
    }
}

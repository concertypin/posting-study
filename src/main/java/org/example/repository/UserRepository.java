package org.example.repository;

import org.example.model.User;

import java.util.List;

public interface UserRepository {

    List<User> findAll();

    User findById(Long id);

    User findByUsername(String username);

    User save(User user);

    boolean deleteById(Long id);
}

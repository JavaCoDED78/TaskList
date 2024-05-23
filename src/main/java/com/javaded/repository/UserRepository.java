package com.javaded.repository;

import com.javaded.domain.user.Role;
import com.javaded.domain.user.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    void update(User user);

    void delete(Long id);

    void create(User user);

    void insertUserRole(Long id, Role role);

    boolean isTaskOwner(Long userId, Long taskId);
}

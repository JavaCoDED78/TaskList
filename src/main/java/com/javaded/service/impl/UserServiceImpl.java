package com.javaded.service.impl;

import com.javaded.domain.user.User;
import com.javaded.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public User getBId(Long id) {
        return null;
    }

    @Override
    public User getByUsername(String username) {
        return null;
    }

    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public boolean isTaskOwner(Long userId, Long taskId) {
        return false;
    }
}

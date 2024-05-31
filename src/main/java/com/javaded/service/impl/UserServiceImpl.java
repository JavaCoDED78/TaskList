package com.javaded.service.impl;

import com.javaded.domain.exception.ResourceNotFoundException;
import com.javaded.domain.mail.MailType;
import com.javaded.domain.user.Role;
import com.javaded.domain.user.User;
import com.javaded.repository.UserRepository;
import com.javaded.service.MailService;
import com.javaded.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Properties;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Override
    @Cacheable(
            value = "UserService::getBId",
            key = "#id"
    )
    public User getById(
            final Long id
    ) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User not found with %s: ", id))
                );
    }

    @Override
    @Cacheable(
            value = "UserService::getByUsername",
            key = "#username"
    )
    public User getByUsername(
            final String username
    ) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User not found with %s: ", username))
                );
    }

    @Override
    @Transactional
    @Caching(cacheable = {
            @Cacheable(
                    value = "UserService::getById",
                    condition = "#user.id!=null",
                    key = "#user.id"
            ),
            @Cacheable(
                    value = "UserService::getByUsername",
                    condition = "#user.username!=null",
                    key = "#user.username"
            )
    })
    public User create(
            final User user
    ) {
        check(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roles = Set.of(Role.ROLE_USER);
        user.setRoles(roles);
        userRepository.save(user);
        mailService.sendEmail(user, MailType.REGISTRATION, new Properties());
        return user;
    }

    private void check(
            final User user
    ) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalStateException(
                    String.format("User already exists with %s: ",
                            user.getUsername())
            );
        }
        if (!user.getPassword().equals(user.getPasswordConfirmation())) {
            throw new IllegalStateException(
                    "Password and password confirmation do not match"
            );
        }
    }

    @Override
    @Transactional
    @Caching(put = {
            @CachePut(
                    value = "UserService::getById",
                    key = "#user.id"
            ),
            @CachePut(
                    value = "UserService::getByUsername",
                    key = "#user.username"
            )
    })
    public User update(
            final User user
    ) {
        User existing = getById(user.getId());
        existing.setName(user.getName());
        user.setUsername(user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional
    @CacheEvict(
            value = "UserService::getBId",
            key = "#id"
    )
    public void delete(
            final Long id
    ) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "UserService::isTaskOwner",
            key = "#userId + '.' + #taskId"
    )
    public boolean isTaskOwner(
            final Long userId,
            final Long taskId
    ) {
        return userRepository.isTaskOwner(userId, taskId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "UserService::getTaskAuthor",
            key = "#taskId"
    )
    public User getTaskAuthor(
            final Long taskId
    ) {
        return userRepository.findTaskAuthor(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));
    }
}

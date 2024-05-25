package com.javaded.repository.impl;

import com.javaded.domain.exception.ResourceMappingException;
import com.javaded.domain.user.Role;
import com.javaded.domain.user.User;
import com.javaded.repository.DataSourceConfig;
import com.javaded.repository.UserRepository;
import com.javaded.repository.mappers.UserRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

//@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final DataSourceConfig dataSourceConfig;

    private final String FIND_BY_ID = """
                        SELECT u.id AS user_id,
                               u.name AS user_name,
                               u.username AS user_username,
                               u.password AS user_password,
                               ur.role AS user_role_role,
                               t.id AS task_id,
                               t.title AS task_title,
                               t.description AS task_description,
                               t.expiration_date AS task_expiration_date,
                               t.status AS task_status
                        FROM users u
                            LEFT JOIN users_roles ur ON u.id = ur.user_id
                            LEFT JOIN users_tasks ut ON u.id = ut.user_id
                            LEFT JOIN tasks t ON ut.task_id = t.id
                        WHERE u.id = ?
            """;

    private final String FIND_BY_USERNAME = """
                        SELECT u.id AS user_id,
                               u.name AS user_name,
                               u.username AS user_username,
                               u.password AS user_password,
                               ur.role AS user_role_role,
                               t.id AS task_id,
                               t.title AS task_title,
                               t.description AS task_description,
                               t.expiration_date AS task_expiration_date,
                               t.status AS task_status
                        FROM users u
                            LEFT JOIN users_roles ur ON u.id = ur.user_id
                            LEFT JOIN users_tasks ut ON u.id = ut.user_id
                            LEFT JOIN tasks t ON ut.task_id = t.id
                        WHERE u.username = ?
            """;

    private final String UPDATE = """
                        UPDATE users
                        SET name = ?,
                            username = ?,
                            password = ?
                        WHERE id = ?
            """;

    private final String CREATE = """
                        INSERT INTO users (name, username, password)
                        VALUES (?, ?, ?)
            """;

    private final String INSERT_USER_ROLE = """
                        INSERT INTO users_roles (user_id, role)
                        VALUES (?, ?)
            """;

    private final String IS_TASK_OWNER = """
             SELECT exists(
                            SELECT 1
                            FROM users_tasks
                            WHERE user_id = ?
                              AND task_id = ?
                        )
            """;

    private final String DELETE = """
                        DELETE FROM users
                        WHERE id = ?
            """;

    @Override
    public Optional<User> findById(Long id) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(FIND_BY_ID,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return Optional.ofNullable(UserRowMapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException(String.format("Exception while finding user by id: %s", id));
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(FIND_BY_USERNAME,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                return Optional.ofNullable(UserRowMapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException(String.format("Exception while finding user by username: %s", username));
        }
    }

    @Override
    public void update(User user) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(UPDATE);
            statement.setString(1, user.getName());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.setLong(4, user.getId());
        } catch (SQLException e) {
            throw new ResourceMappingException("Exception while updating user.");
        }
    }

    @Override
    public void create(User user) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getName());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                rs.next();
                user.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Exception while creating user.");
        }
    }

    @Override
    public void insertUserRole(Long userId, Role role) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(INSERT_USER_ROLE);
            statement.setLong(1, userId);
            statement.setString(2, role.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Exception while inserting user role.");
        }
    }

    @Override
    public boolean isTaskOwner(Long userId, Long taskId) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(IS_TASK_OWNER);
            statement.setLong(1, userId);
            statement.setLong(2, taskId);
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Exception while checking if user is task owner..");
        }
    }

    @Override
    public void delete(Long id) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(DELETE);
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Exception while deleting user.");
        }
    }
}

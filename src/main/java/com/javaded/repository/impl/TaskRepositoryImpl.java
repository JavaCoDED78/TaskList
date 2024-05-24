package com.javaded.repository.impl;

import com.javaded.domain.exception.ResourceMappingException;
import com.javaded.domain.task.Task;
import com.javaded.repository.DataSourceConfig;
import com.javaded.repository.TaskRepository;
import com.javaded.repository.mappers.TaskRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TaskRepositoryImpl implements TaskRepository {

    private final DataSourceConfig dataSourceConfig;

    private final String FIND_BY_ID = """
                        SELECT t.id              AS task_id,
                               t.title           AS task_title,
                               t.description     AS task_description,
                               t.expiration_date AS task_expiration_date,
                               t.status          AS task_status
                        FROM tasks t
                        WHERE t.id = ?
            """;

    private final String FIND_ALL_BY_USER_ID = """
                        SELECT t.id              AS task_id,
                               t.title           AS task_title,
                               t.description     AS task_description,
                               t.expiration_date AS task_expiration_date,
                               t.status          AS task_status
                        FROM tasks t
                                 JOIN users_tasks ut ON t.id = ut.task_id
                        WHERE ut.user_id = ?
            """;

    private final String ASSIGN = """
                        INSERT INTO users_tasks (task_id, user_id)
                        VALUES (?, ?)
            """;

    private final String UPDATE = """
                        UPDATE tasks
                        SET title = ?,
                            description = ?,
                            expiration_date = ?,
                            status = ?
                        WHERE id = ?
            """;

    private final String CREATE = """
                        INSERT INTO tasks (title, description, expiration_date, status)
                        VALUES (?, ?, ?, ?)
            """;

    private final String DELETE = """
                        DELETE FROM tasks
                        WHERE id = ?
            """;

    @Override
    public Optional<Task> findById(Long id) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(FIND_BY_ID);
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return Optional.ofNullable(TaskRowMapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException(String.format("Exception while finding task by id: %s", id));
        }
    }

    @Override
    public List<Task> findAllByUserId(Long userId) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(FIND_ALL_BY_USER_ID);
            statement.setLong(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                return TaskRowMapper.mapRows(rs);
            }
        } catch (SQLException e) {
            throw new ResourceMappingException(String.format("Error while finding all by user id: %s", userId));
        }
    }

    @Override
    public void assignToUserById(Long taskId, Long userId) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(ASSIGN);
            statement.setLong(1, taskId);
            statement.setLong(2, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException(String.format("Error while assigning to user id: %s", userId));
        }
    }

    @Override
    public void update(Task task) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(UPDATE);
            statement.setString(1, task.getTitle());
            if (task.getDescription() == null) {
                statement.setNull(2, Types.VARCHAR);
            } else {
                statement.setString(2, task.getDescription());
            }
            if (task.getExpirationDate() == null) {
                statement.setNull(3, Types.TIMESTAMP);
            } else {
                statement.setTimestamp(3, Timestamp.valueOf(task.getExpirationDate()));
            }
            statement.setString(4, task.getStatus().name());
            statement.setLong(5, task.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while updating task.");
        }
    }

    @Override
    public void create(Task task) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, task.getTitle());
            if (task.getDescription() == null) {
                statement.setNull(2, Types.VARCHAR);
            } else {
                statement.setString(2, task.getDescription());
            }
            if (task.getExpirationDate() == null) {
                statement.setNull(3, Types.TIMESTAMP);
            } else {
                statement.setTimestamp(3, Timestamp.valueOf(task.getExpirationDate()));
            }
            statement.setString(4, task.getStatus().name());
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                rs.next();
                task.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while creating task.");
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
            throw new ResourceMappingException(String.format("Error while deleting task by id: %s", id));
        }
    }
}

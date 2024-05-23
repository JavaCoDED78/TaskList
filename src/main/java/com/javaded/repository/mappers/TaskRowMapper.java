package com.javaded.repository.mappers;

import com.javaded.domain.task.Status;
import com.javaded.domain.task.Task;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskRowMapper {

    @SneakyThrows
    public static Task mapRow(ResultSet resultSet) {
        if (resultSet.next()) {
            Task task = new Task();
            task.setId(resultSet.getLong("task_id"));
            build(resultSet, task);
            return task;
        }
        return null;
    }

    @SneakyThrows
    public static List<Task> mapRows(ResultSet resultSet) {
        List<Task> tasks = new ArrayList<>();
        while (resultSet.next()) {
            Task task = new Task();
            task.setId(resultSet.getLong("task_id"));
            if (!resultSet.wasNull()) {
                build(resultSet, task);
                tasks.add(task);
            }
        }
        return tasks;
    }

    private static void build(ResultSet resultSet, Task task) throws SQLException {
        task.setTitle(resultSet.getString("task_title"));
        task.setDescription(resultSet.getString("task_description"));
        task.setStatus(Status.valueOf(resultSet.getString("task_status")));
        Timestamp expirationDateTimestamp = resultSet.getTimestamp("task_expiration_date");
        LocalDateTime expirationDate = expirationDateTimestamp != null ? expirationDateTimestamp.toLocalDateTime() : null;
        task.setExpirationDate(expirationDate);
    }

}

CREATE TABLE IF NOT EXISTS users
(
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS tasks
(
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    description     VARCHAR(255) NULL,
    status          VARCHAR(255) NOT NULL,
    expiration_date TIMESTAMP    NULL
);

CREATE TABLE IF NOT EXISTS users_tasks
(
    user_id BIGINT NOT NULL,
    task_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, task_id),
    CONSTRAINT fk_users_tasks_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE NO ACTION,
    CONSTRAINT fk_users_tasks_tasks FOREIGN KEY (task_id) REFERENCES tasks (id) ON DELETE CASCADE ON UPDATE NO ACTION
);

CREATE TABLE IF NOT EXISTS users_roles
(
    user_id BIGINT       NOT NULL,
    role    VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_users_roles_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE NO ACTION
);
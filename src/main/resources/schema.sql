CREATE TABLE IF NOT EXISTS task_metric (
    id long AUTO_INCREMENT PRIMARY KEY,
    task VARCHAR(255),
    total_time BIGINT,
    count BIGINT
);


/*
 * (C): cowparlour.com  2025
 */
package com.cowparlour.cp.task.repository;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

/**
 * Spring Data JDBC Datastore used to store task metrics
 */
public interface DataStore extends CrudRepository<TaskMetric, String> {

    Optional<TaskMetric> findByTask(String task);
}

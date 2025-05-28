package com.cowparlour.cp.task.repository;

import org.springframework.data.repository.CrudRepository;

public interface DataStore extends CrudRepository<TaskMetric, String> {
}

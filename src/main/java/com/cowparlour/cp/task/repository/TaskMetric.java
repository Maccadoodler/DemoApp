/*
 * (C): cowparlour.com  2025
 */
package com.cowparlour.cp.task.repository;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;

/**
 * Task metric table in the datastore
 * @param id            primary key
 * @param task          task ID
 * @param totalTime     total times of all the task with this ids run
 * @param count         number of times this task has run
 */
@Table("TASK_METRIC")
public record TaskMetric(
        @Id Long id,

        String task,

        BigInteger totalTime,

        BigInteger count
) {}

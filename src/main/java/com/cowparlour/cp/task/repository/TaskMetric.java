package com.cowparlour.cp.task.repository;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;

@Table("TASK_METRIC")
public record TaskMetric(
        @Id String task,

        BigInteger totalTime,

        BigInteger count
) {}

package com.cowparlour.cp.task.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigInteger;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
public class TaskMetricTest {

    @Autowired
    private DataStore repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testFindTask() {
        TaskMetric record = new TaskMetric(null, "John", BigInteger.valueOf(100), BigInteger.TEN);
        repository.save(record);

        Optional<TaskMetric> found = repository.findByTask("John");
        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(1L);
        assertThat(found.get().count()).isEqualTo(BigInteger.TEN);
        assertThat(found.get().totalTime()).isEqualTo(BigInteger.valueOf(100));
    }

}

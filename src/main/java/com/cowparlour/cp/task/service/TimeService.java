package com.cowparlour.cp.task.service;

import com.cowparlour.cp.task.dto.Average;
import com.cowparlour.cp.task.dto.TaskTime;
import com.cowparlour.cp.task.repository.DataStore;
import com.cowparlour.cp.task.repository.TaskMetric;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Optional;

@Service
public class TimeService {

    private static final Logger logger = LoggerFactory.getLogger(TimeService.class);

    private final DataStore dataStore;

    public TimeService(@Nonnull DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Nonnull
    public Optional<Average> getAverage(@Nonnull String task) {

        Optional<Average> result;

        logger.info("+ GetAverage");
        try {
            Optional<TaskMetric> taskData = dataStore.findById(task);
            result = taskData.map(TaskMetric -> new Average(TaskMetric.task(),
                    TaskMetric.totalTime().divide(TaskMetric.count())));
         } catch (DataAccessException e) {

            throw new ServiceFailure("Problem with reading Datastore", e);
        }

        return result;
    }

    @Transactional
    public void updateTask(@Nonnull TaskTime data) {

        logger.info("+ UpdateTask");

        TaskMetric details;

        try {
            Optional<TaskMetric> current = dataStore.findById(data.task());
            if (current.isPresent()) {
                BigInteger count = current.get().count().add(BigInteger.valueOf(1));
                BigInteger totalTime = current.get().totalTime().add(data.duration());
                details = new TaskMetric(data.task(), totalTime, count);


            } else {
                details = new TaskMetric(data.task(), data.duration(), BigInteger.valueOf(1));

            }
        } catch (DataAccessException e) {
            logger.debug("Update Problem - :" + e.getMessage());
            throw new ServiceFailure("Problem with updating Datastore", e);
        }

        saveOrInsert(details);
    }


    public void saveOrInsert(@Nonnull TaskMetric metric) {

        if (dataStore.existsById(metric.task())) {
            dataStore.save(metric);
        } else {
            dataStore.save(metric);
        }
    }

}

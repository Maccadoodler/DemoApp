/*
 * (C): cowparlour.com  2025
 */
package com.cowparlour.cp.task.service;

import com.cowparlour.cp.task.dto.Average;
import com.cowparlour.cp.task.dto.TaskTime;
import com.cowparlour.cp.task.repository.DataStore;
import com.cowparlour.cp.task.repository.TaskMetric;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Optional;

/**
 * Service Bean to process how the averages are stored and processed
 */

@Service
public class TimeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeService.class);

    private final DataStore dataStore;

    public TimeService(@Nonnull DataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Checks the Datastore to see if there is a record for the given task.
     * if so returns the average duration
     * @param task      ID of the task that is being queried
     * @return          Optional DTO of the average duration for the given task
     */
    @Nonnull
    public Optional<Average> getAverage(@Nonnull String task) {

        Optional<Average> result;

        LOGGER.info("+ GetAverage");
        try {
            Optional<TaskMetric> taskData = dataStore.findByTask(task);
            if (taskData.isPresent()) {
                result = taskData.map(u -> new Average(u.task(),
                        u.totalTime().divide(u.count())));
            } else {
                result = Optional.empty();
            }

        } catch (RuntimeException e) {

            throw new ServiceFailure("Problem with reading Datastore", e);
        }

        return result;
    }

    /**
     * Updates the datastore with taskTime data.
     * This is transactional read/update function
     * @param data      new duration for a given task
     */
    @Transactional
    public void updateTask(@Nonnull TaskTime data) {
        TaskMetric details;

        LOGGER.info("+ UpdateTask");
        try {
            Optional<TaskMetric> current = dataStore.findByTask(data.task());
            if (current.isPresent()) {
                BigInteger count = current.get().count().add(BigInteger.valueOf(1));
                BigInteger totalTime = current.get().totalTime().add(data.duration());
                details = new TaskMetric(current.get().id(),data.task(), totalTime, count);

            } else {
                details = new TaskMetric(null,data.task(), data.duration(), BigInteger.valueOf(1));
            }
            dataStore.save(details);
        } catch (RuntimeException e) {

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Update Problem - :" + e.getMessage());
            }

            throw new ServiceFailure("Problem with updating Datastore", e);
        }

    }

}

/*
 * (C): cowparlour.com  2025
 */

package com.cowparlour.cp.task.controller;

import com.cowparlour.cp.task.dto.Average;
import com.cowparlour.cp.task.dto.TaskTime;
import com.cowparlour.cp.task.service.ServiceFailure;
import com.cowparlour.cp.task.service.TimeService;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Rest controller class
 */

@RestController
@RequestMapping("/time")
public class TimeController {

    private static final Logger logger = LoggerFactory.getLogger(TimeController.class);
    private final TimeService timeService;

    public TimeController(@Nonnull TimeService timeService) {
        this.timeService = timeService;
    }

    /**
     * Endpoint to return the average time of recorded tasks created by
     * the specified task
     * @param task  The String ID of the task to get the Average Time of
     * @return      Average DTO object
     */
    @GetMapping("/average/{task}")
    @Nonnull
    public ResponseEntity<?> getAverage(@PathVariable String task) {

        ResponseEntity<?> response;

        try {
            Optional<Average> average = timeService.getAverage(task);

            if (average.isPresent()) {
                response = ResponseEntity.ok(average);
            } else {
                response = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                logger.error("Not found");

            }
        } catch (ServiceFailure e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error");
        }
        return response;
    }

    /**
     * Post endpoint to record task metrics.
     * @param request   TaskTime DTO that contains the json information to record
     *                  task and duration.
     * @return          A HTTP status code with a message
     */
    @PostMapping("/record")
    public ResponseEntity<String> recordTask(@RequestBody TaskTime request) {

        try {
            timeService.updateTask(request);
            return ResponseEntity.ok(String.format("Task %s processed successfully.", request.task()));

        } catch (ServiceFailure e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error");
        }
    }

}

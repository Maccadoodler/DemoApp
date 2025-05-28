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

@RestController
@RequestMapping("/time")
public class TimeController {

    private static final Logger logger = LoggerFactory.getLogger(TimeController.class);
    private final TimeService timeService;

    public TimeController(@Nonnull TimeService timeService) {
        this.timeService = timeService;
    }

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
            response = ResponseEntity.internalServerError().build();
            logger.debug("Unknown error - : " + e.getMessage());
        }

        return response;
    }


    @PostMapping("/record")
    public ResponseEntity<String> recordTask(@RequestBody TaskTime request) {

        try {
            timeService.updateTask(request);
            return ResponseEntity.ok(String.format("Task %s processed successfully.", request.task()));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error");
        }
    }

}

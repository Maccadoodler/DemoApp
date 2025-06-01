/*
 * (C): cowparlour.com  2025
 */
package com.cowparlour.cp.task.dto;

import jakarta.annotation.Nonnull;
import java.math.BigInteger;

/**
 * DTO used to return the average duration for the given task
 * @param task                  Id of the task
 * @param average              the average time a task takes to complete
 */
@Nonnull
public record Average(@Nonnull String task, BigInteger average) {

}

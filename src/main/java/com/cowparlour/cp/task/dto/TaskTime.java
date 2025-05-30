/*
 * (C): cowparlour.com  2025
 */
package com.cowparlour.cp.task.dto;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.math.BigInteger;

/**
 * DTO used for passing the actual time spent in executing this particular
 * task operation
 * @param task              id of task processed
 * @param duration          length of processing.
 */
@Nonnull
public record TaskTime(@Nullable String task, BigInteger duration) {
}

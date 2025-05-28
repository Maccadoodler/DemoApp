package com.cowparlour.cp.task.dto;

import jakarta.annotation.Nonnull;

import java.math.BigInteger;

@Nonnull
public record Average(@Nonnull String task, BigInteger averageDuration) {

}

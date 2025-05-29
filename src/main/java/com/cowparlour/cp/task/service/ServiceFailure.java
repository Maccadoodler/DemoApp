/*
 * (C): cowparlour.com  2025
 */

package com.cowparlour.cp.task.service;

/**
 * Domain specific exception.
 */
public class ServiceFailure extends RuntimeException {
    public ServiceFailure(String message) {
        super(message);
    }

    public ServiceFailure(String message, Exception e) {
        super(message, e);
    }

}

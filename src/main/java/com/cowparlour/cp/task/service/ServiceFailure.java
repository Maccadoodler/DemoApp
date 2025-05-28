package com.cowparlour.cp.task.service;

public class ServiceFailure extends RuntimeException {
    public ServiceFailure(String message) {
        super(message);
    }

    public ServiceFailure(String message, Exception e) {
        super(message, e);
    }

}

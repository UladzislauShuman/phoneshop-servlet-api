package com.es.phoneshop.model.exceptions;

public class InsertDemoDataException extends RuntimeException {
    private static final String INITIALIZATION_PROBLEM_MESSAGE = "Problem with DemoDate initialization: %s";

    public InsertDemoDataException(String message) {
        super(String.format(INITIALIZATION_PROBLEM_MESSAGE, message));
    }
}

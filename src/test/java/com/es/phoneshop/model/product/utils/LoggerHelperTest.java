package com.es.phoneshop.model.product.utils;

import com.es.phoneshop.utils.LoggerHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class LoggerHelperTest {

    @Mock
    private Logger mockLogger;

    public static final String TEST_MESSAGE = "Test";
    public static final String TEST_EXCEPTION_MESSAGE = "Test Exception";
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogInitWithMessage() {

        LoggerHelper.logInit(mockLogger, TEST_MESSAGE);

        verify(mockLogger, times(1)).debug(LoggerHelper.INIT + TEST_MESSAGE);
    }

    @Test
    void testLogInitWithException() {
        Exception testException = new Exception(TEST_EXCEPTION_MESSAGE);

        LoggerHelper.logInit(mockLogger, testException);

        verify(mockLogger, times(1)).debug(LoggerHelper.INIT +
                getFormatException(testException) + TEST_EXCEPTION_MESSAGE);
    }

    @Test
    void testLogDoGetWithMessage() {

        LoggerHelper.logDoGet(mockLogger, TEST_MESSAGE);

        verify(mockLogger, times(1)).debug(LoggerHelper.DO_GET + TEST_MESSAGE);
    }

    @Test
    void testLogDoGetWithException() {
        Exception testException = new Exception(TEST_EXCEPTION_MESSAGE);

        LoggerHelper.logDoGet(mockLogger, testException);

        verify(mockLogger, times(1)).debug(LoggerHelper.DO_GET +
                getFormatException(testException) + TEST_EXCEPTION_MESSAGE);
    }

    @Test
    void testLogDoPostWithMessage() {

        LoggerHelper.logDoPost(mockLogger, TEST_MESSAGE);

        verify(mockLogger, times(1)).debug(LoggerHelper.DO_POST + TEST_MESSAGE);
    }

    @Test
    void testLogDoPostWithException() {
        Exception testException = new Exception(TEST_EXCEPTION_MESSAGE);

        LoggerHelper.logDoPost(mockLogger, testException);

        verify(mockLogger, times(1)).debug(LoggerHelper.DO_POST +
                getFormatException(testException) + TEST_EXCEPTION_MESSAGE);
    }

    @Test
    void testLogDoPostWithAnotherException() {
        Exception testException = new RuntimeException(TEST_EXCEPTION_MESSAGE);

        LoggerHelper.logDoPost(mockLogger, testException);

        verify(mockLogger, times(1)).debug(LoggerHelper.DO_POST +
                getFormatException(testException) + TEST_EXCEPTION_MESSAGE);
    }

    private String getFormatException(Exception e) {
        return e.getClass().getSimpleName() + ": ";
    }
}

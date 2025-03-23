package com.es.phoneshop.model.product.services;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.exceptions.InsertDemoDataException;
import com.es.phoneshop.model.product.exceptions.ProductNotFoundException;
import com.es.phoneshop.web.listeners.DemoDataInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DemoDataInitializerTest {
    private static final String TEST_MESSAGE = "Test";

    @Mock
    private ProductDao productDao;
    @Mock
    private ServletContextEvent servletContextEvent;
    @Mock
    private ServletContext servletContext;

    @Spy
    @InjectMocks
    private DemoDataInitializer demoDataInitializer;

    @BeforeEach
    void setUp() {
        when(servletContextEvent.getServletContext()).thenReturn(servletContext);
    }

    @Test
    void initialize_insertDemoDataTrue() {
        when(servletContext.getInitParameter(DemoDataInitializer.INSERT_DEMO_DATA)).thenReturn("true");

        demoDataInitializer.initialize(servletContextEvent);

        verify(productDao, times(16)).save(any());
    }

    @Test
    void initialize_insertDemoDataFalse() {
        when(servletContext.getInitParameter(DemoDataInitializer.INSERT_DEMO_DATA)).thenReturn("false");

        demoDataInitializer.initialize(servletContextEvent);

        verify(productDao, never()).save(any(Product.class));
    }

    @Test
    void initialize_insertDemoDataNull() {
        when(servletContext.getInitParameter(DemoDataInitializer.INSERT_DEMO_DATA)).thenReturn(null);
        assertDoesNotThrow(() -> demoDataInitializer.initialize(servletContextEvent));
    }



    @Test
    void initialize_productDaoSaveThrowsException() throws ProductNotFoundException {
        when(servletContext.getInitParameter(DemoDataInitializer.INSERT_DEMO_DATA)).thenReturn("true");
        doThrow(new ProductNotFoundException(TEST_MESSAGE)).when(productDao).save(any());

        assertThrows(RuntimeException.class, () -> demoDataInitializer.initialize(servletContextEvent));
    }

    @Test
    void initialize_getInitParameterThrowsException() {
        when(servletContext.getInitParameter(DemoDataInitializer.INSERT_DEMO_DATA)).thenThrow(new NullPointerException(TEST_MESSAGE));

        assertThrows(RuntimeException.class, () -> demoDataInitializer.initialize(servletContextEvent));
    }
}

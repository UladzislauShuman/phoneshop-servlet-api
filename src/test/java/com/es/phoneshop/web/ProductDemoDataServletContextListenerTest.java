package com.es.phoneshop.web;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Spy;

import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductDemoDataServletContextListenerTest {

    private static final String FAIL_OTHER_EXCEPTION = "Fail Other Exception";
    @Mock
    private ServletContextEvent servletContextEvent;
    @Mock
    private ServletContext servletContext;
    @Mock
    private ProductDao productDao;

    @Spy // Use Spy instead of InjectMocks
    @InjectMocks
    private ProductDemoDataServletContextListener listener;

    @BeforeEach
    void setUp() {}

    @Test
    void testContextInitializedInsertDemoDataTrue() throws Exception {
        when(servletContextEvent.getServletContext()).thenReturn(servletContext);
        when(servletContext.getInitParameter(ProductDemoDataServletContextListener.INSERT_DEMO_DATA)).thenReturn("true");
        when(listener.getSampleProducts()).thenReturn(List.of(new Product(), new Product()));

        List<Product> sampleProducts = listener.getSampleProducts();
        listener.contextInitialized(servletContextEvent);

        verify(productDao, times(2)).save(any(Product.class));
    }
}

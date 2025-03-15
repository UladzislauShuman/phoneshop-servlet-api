package com.es.phoneshop.web;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.exceptions.ProductNotFoundException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductDetailsPageServletTest {
    private static final String TEST_MESSAGE = "TEST";

    @Mock
    private ServletConfig config;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ProductDao productDao;
    @Mock
    private RequestDispatcher requestDispatcher;

    @InjectMocks
    private ProductDetailsPageServlet servlet;

    private final String TEST_BAD_ID = "123";
    private final String TEAR_INVALID_ID = "/aaaa";
    private final String TEST_GOOD_ID = "/1";

    @BeforeEach
    public void setup() throws ServletException {
        this.servlet.init(this.config);
    }

    @Test
    public void testDoubleInit() throws ServletException {
        ProductDetailsPageServlet testServlet = new ProductDetailsPageServlet();
        testServlet.init(config);
        testServlet.init(config);
        verifyNoMoreInteractions(productDao);
    }

    @Test
    public void testCatchProductNotFoundException() throws ServletException, IOException {
        when(this.productDao.getProduct(any())).thenThrow(new ProductNotFoundException(TEST_MESSAGE));
        when(request.getPathInfo()).thenReturn("/" + TEST_BAD_ID);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(response).setStatus(eq(HttpServletResponse.SC_NOT_FOUND));
        verify(request).setAttribute(eq(ProductDetailsPageServlet.ATTRIBUTE_PRODUCTCODE), eq(TEST_BAD_ID));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testCatchNumberFormatException() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn(this.TEAR_INVALID_ID);

        servlet.doGet(request,response);

        verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND), eq(ProductDetailsPageServlet.INVALID_ID_FORMAT));
    }

    @Test
    public void testWithGoodId() throws ServletException, IOException {

        Product testProduct = new Product();
        testProduct.setId(1L);
        when(request.getPathInfo()).thenReturn(this.TEST_GOOD_ID);
        when(productDao.getProduct(eq(1L))).thenReturn(testProduct);
        when(request.getRequestDispatcher(eq(ProductDetailsPageServlet.PRODUCT_JSP_PATH))).thenReturn(requestDispatcher);

        servlet.doGet(request,response);

        verify(request).setAttribute(eq(ProductDetailsPageServlet.ATTRIBUTE_PRODUCT), eq(testProduct));
        verify(requestDispatcher).forward(request, response);
    }
}



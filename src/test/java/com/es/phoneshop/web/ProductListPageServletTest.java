package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
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
import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductListPageServletTest {
    private static final String FORWARD_EXCEPTION = "Forward Exception";
    private static final String EXCEPTION_TEST_MESSAGE = "Test Exception";
    private static final String FAIL_MESSAGE_PRODUCTDAO_FIELD = "cant take productDaoField";

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig config;
    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductListPageServlet servlet;

    @BeforeEach
    public void setup() throws ServletException { //
        this.servlet.init(this.config);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        when(this.request.getRequestDispatcher(anyString())).thenReturn(this.requestDispatcher);
        this.servlet.doGet(this.request, this.response);

        verify(this.requestDispatcher).forward(this.request, this.response);
        verify(this.request).setAttribute(eq(ProductListPageServlet.ATTRIBUTE), any());
    }

    @Test
    public void testDoGetForwardsToProductListJsp() throws  ServletException, IOException {
        when(this.request.getRequestDispatcher(anyString())).thenReturn(this.requestDispatcher);
        final String expectedPath = ProductListPageServlet.PRODUCTLIST_JSP_PATH;
        this.servlet.doGet(this.request, this.response);

        verify(this.request).getRequestDispatcher(expectedPath);
        verify(this.requestDispatcher).forward(this.request, this.response);
    }

    @Test
    public void testDoGetWithNullRequestDispatcher() throws ServletException, IOException {
        when(request.getRequestDispatcher(anyString())).thenReturn(null);
        servlet.doGet(request, response);
                                //проверка на равенство с
        verify(response).sendError(eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), anyString());
    }

    @Test
    public void testDoGetHandleForwardException() throws ServletException, IOException {
        when(request.getRequestDispatcher(anyString())).thenReturn((this.requestDispatcher));
        doThrow(
                new ServletException(FORWARD_EXCEPTION))
                .when(this.requestDispatcher)
                .forward(this.request, this.response
                );
        this.servlet.doGet(this.request, this.response);
    }
    @Test
    public void testDoGetHandleIOExcteption() throws ServletException, IOException {
        when(this.request.getRequestDispatcher(anyString()))
                .thenReturn(this.requestDispatcher);
        doThrow(new IOException(FORWARD_EXCEPTION))
                .when(this.requestDispatcher)
                .forward(this.request, this.response);
        this.servlet.doGet(this.request, this.response);
    }

    @Test
    public void testDoGettCallRequestDispatherOnce() throws ServletException, IOException {
        this.servlet.doGet(this.request, this.response);
        verify(this.request, times(1)).getRequestDispatcher(
                ProductListPageServlet.PRODUCTLIST_JSP_PATH
        );
    }

    @Test
    public void testDoGetWithEmptyProductList() throws ServletException, IOException {
        when(this.request.getRequestDispatcher(anyString())).thenReturn(this.requestDispatcher);
        when(this.productDao.findProducts(null, null, null)).thenReturn(Collections.emptyList());
        this.servlet.doGet(this.request, this.response);
        verify(this.request).setAttribute(ProductListPageServlet.ATTRIBUTE, Collections.emptyList());
        verify(this.requestDispatcher).forward(this.request, this.response);
    }

    @Test
    public void testDoGetWithNullList() throws ServletException, IOException {
        when(this.request.getRequestDispatcher(anyString())).thenReturn(this.requestDispatcher);
        when(this.productDao.findProducts(null, null, null)).thenReturn(null);
        this.servlet.doGet(this.request, this.response);
        verify(this.request).setAttribute(ProductListPageServlet.ATTRIBUTE, null);
        verify(this.requestDispatcher).forward(this.request, this.response);
    }

    @Test
    public void testDoGetHandlesProductDaoException() throws ServletException, IOException {
        when(this.productDao.findProducts(anyString(), anyString(), anyString())).thenThrow(new RuntimeException(EXCEPTION_TEST_MESSAGE));
        servlet.doGet(this.request, this.response);
        // должно было перейти в catch(Exception), где и должен быть второй аргумент пустым списком
        verify(request).setAttribute(eq(ProductListPageServlet.ATTRIBUTE), argThat(argument -> argument instanceof java.util.List && ((java.util.List<?>) argument).isEmpty()));
        verify(requestDispatcher, never()).forward(this.request, this.response); // тогда выбрасывается исключение и дальше не продолжается
        verify(response).sendError(eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), anyString());
    }

    @Test
    public void testDoubleInit() throws ServletException {
        ProductListPageServlet testServlet = new ProductListPageServlet();
        testServlet.init(this.config);
        testServlet.init(this.config);
        verifyNoMoreInteractions(this.productDao);
    }

    @Test
    public void testInitInitializesProductDao() throws ServletException {
        ProductListPageServlet productListPageServlet = new ProductListPageServlet();
        productListPageServlet.init(this.config);

        try {
            Field productDaoField = ProductListPageServlet.class.getDeclaredField("productDao");
            productDaoField.setAccessible(true);
            ProductDao productDaoValue = (ProductDao) productDaoField.get(productListPageServlet);

            assertNotNull(productDaoValue);
            assertTrue(productDaoValue instanceof ArrayListProductDao);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_MESSAGE_PRODUCTDAO_FIELD);
        }
    }
}
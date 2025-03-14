package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.ServletConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class ProductListPageServletTest {
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

    @Before
    public void setup() throws ServletException { //
        this.servlet.init(this.config);
        when(this.request.getRequestDispatcher(anyString())).thenReturn(this.requestDispatcher);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        this.servlet.doGet(this.request, this.response);

        verify(this.requestDispatcher).forward(this.request, this.response);
        verify(this.request).setAttribute(eq("products"), any());
    }

    @Test
    public void testDoGetForwardsToProductListJsp() throws  ServletException, IOException {
        final String expectedPath = "/WEB-INF/pages/productList.jsp";
        this.servlet.doGet(this.request, this.response);

        verify(this.request).getRequestDispatcher(expectedPath);
        verify(this.requestDispatcher).forward(this.request, this.response);
    }

    @Test(expected = NullPointerException.class)
    public void testDoGetWithNullRequestDispatcher()  throws ServletException, IOException {
        when(request.getRequestDispatcher(anyString())).thenReturn(null);
        this.servlet.doGet(this.request, this.response);
    }

    @Test
    public void testDoGetHandleForwardException() throws ServletException, IOException {
        when(request.getRequestDispatcher(anyString())).thenReturn((this.requestDispatcher));
        doThrow(
                new ServletException("Forward Exception"))
                .when(this.requestDispatcher)
                .forward(this.request, this.response
                );
        this.servlet.doGet(this.request, this.response);
    }
    @Test
    public void testDoGetHandleIOExcteption() throws ServletException, IOException {
        when(this.request.getRequestDispatcher(anyString()))
                .thenReturn(this.requestDispatcher);
        doThrow(new IOException("Forward Exception"))
                .when(this.requestDispatcher)
                .forward(this.request, this.response);
        this.servlet.doGet(this.request, this.response);
    }

    @Test
    public void testDoGettCallRequestDispatherOnce() throws ServletException, IOException {
        this.servlet.doGet(this.request, this.response);
        verify(this.request, times(1)).getRequestDispatcher(
                "/WEB-INF/pages/productList.jsp"
        );
    }

    @Test
    public void testDoGetWithEmptyProductList() throws ServletException, IOException {
        when(this.productDao.findProducts()).thenReturn(Collections.emptyList());
        this.servlet.doGet(this.request, this.response);
        verify(this.request).setAttribute("products", Collections.emptyList());
        verify(this.requestDispatcher).forward(this.request, this.response);
    }

    @Test
    public void testDoGetWithNullList() throws ServletException, IOException {
        when(this.productDao.findProducts()).thenReturn(null);
        this.servlet.doGet(this.request, this.response);
        verify(this.request).setAttribute("products", null);
        verify(this.requestDispatcher).forward(this.request, this.response);
    }

    @Test
    public void testDoGetHandlesProductDaoException() throws ServletException, IOException {
        when(this.productDao.findProducts()).thenThrow(
                new RuntimeException("Test Exception")
        );

        try {
            this.servlet.doGet(this.request, this.response);
        } catch (RuntimeException e) {
            fail("fail");
        }

        verify(this.request).setAttribute(eq("products"), any());
        verify(this.requestDispatcher).forward(this.request, this.response);
    }

    @Test
    public void testDoubleInit() throws ServletException {
        this.servlet.init(this.config);
        this.servlet.init(this.config);
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
            fail("cant take productDaoField");
        }
    }
}
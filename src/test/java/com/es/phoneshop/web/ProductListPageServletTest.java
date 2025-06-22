package com.es.phoneshop.web;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.recentlyviewed.RecentlyViewedProducts;
import com.es.phoneshop.model.recentlyviewed.RecentlyViewedProductsService;
import com.es.phoneshop.web.listeners.DependenciesServletContextListener;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;

import static com.es.phoneshop.web.ProductListPageServlet.ATTRIBUTE_PRODUCTS;
import static com.es.phoneshop.web.ProductListPageServlet.ATTRIBUTE_RECENTLY_PRODUCTS;
import static com.es.phoneshop.web.ProductListPageServlet.PRODUCT_LIST_JSP_PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductListPageServletTest {
    private static final String FORWARD_EXCEPTION = "Forward Exception";

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig config;
    @Mock
    private ServletContext servletContext;
    @Mock
    private ProductDao productDao;
    @Mock
    private RecentlyViewedProductsService recentlyViewedProductsService;
    @Mock
    private HttpSession session;
    @Mock
    private RecentlyViewedProducts recentlyViewedProducts;

    @InjectMocks
    private ProductListPageServlet servlet;

    @Mock
    private Product product;
    @BeforeEach
    void setUp() throws ServletException {
        when(config.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(DependenciesServletContextListener.ATTRIBUTE_PRODUCT_DAO)).thenReturn(productDao);
        when(servletContext.getAttribute(DependenciesServletContextListener.ATTRIBUTE_RECENTLY_VIEWED_PRODUCTS_SERVICE)).thenReturn(recentlyViewedProductsService);
        when(request.getRequestDispatcher(PRODUCT_LIST_JSP_PATH)).thenReturn(requestDispatcher);
        when(productDao.findProducts(anyString(), anyString(), anyString())).thenReturn(Collections.emptyList());
        when(recentlyViewedProductsService.createRecentlyViewedProducts(any())).thenReturn(recentlyViewedProducts);
        when(recentlyViewedProducts.getRecentlyViewedProductsList()).thenReturn(Collections.emptyList());
        when(request.getSession()).thenReturn(session);
        servlet.init(config);
    }

    @Test
    void doGet_success() throws IOException, ServletException {

        servlet.doGet(request, response);

        verify(request).setAttribute(ATTRIBUTE_PRODUCTS, Collections.emptyList());
        verify(request).setAttribute(ATTRIBUTE_RECENTLY_PRODUCTS, Collections.emptyList());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void doGet_exception() throws IOException, ServletException {
        doThrow(new ServletException(FORWARD_EXCEPTION)).when(requestDispatcher).forward(request, response);

        servlet.doGet(request,response);

        verify(response).sendError(eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), anyString());
    }

}

package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProducts;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProductsService;
import com.es.phoneshop.model.product.recentlyviewed.storage.HttpSessionRecentlyViewedProductsStorage;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.util.Collections;

import static com.es.phoneshop.web.ProductListPageServlet.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
public class ProductListPageServletTest {
    private static final String FORWARD_EXCEPTION = "Forward Exception";
    private static final String EXCEPTION_TEST_MESSAGE = "Test Exception";

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
    private HttpSession httpSession;
    @Mock
    private RecentlyViewedProducts recentlyViewedProducts;

    @InjectMocks
    private ProductListPageServlet servlet;

    @Test
    public void testDoGet() throws ServletException, IOException {
        when(request.getSession()).thenReturn(httpSession);
        when(request.getRequestDispatcher(anyString())).thenReturn(this.requestDispatcher);
        when(recentlyViewedProductsService.getRecentlyViewedProductsFromStorage(any(HttpSessionRecentlyViewedProductsStorage.class))).thenReturn(recentlyViewedProducts);
        when(recentlyViewedProducts.getRecentlyViewedProductsList()).thenReturn(Collections.emptyList());

        this.servlet.doGet(this.request, this.response);

        verify(this.requestDispatcher).forward(this.request, this.response);
        verify(this.request).setAttribute(eq(ProductListPageServlet.ATTRIBUTE_PRODUCTS), any());
        verify(this.request).setAttribute(eq(ProductListPageServlet.ATTRIBUTE_RECENTLY_PRODUCTS), any());
    }

    @Test
    public void testDoGetForwardsToProductListJsp() throws  ServletException, IOException {
        when(request.getSession()).thenReturn(httpSession);
        when(this.request.getRequestDispatcher(anyString())).thenReturn(this.requestDispatcher);
        when(recentlyViewedProductsService.getRecentlyViewedProductsFromStorage(any(HttpSessionRecentlyViewedProductsStorage.class))).thenReturn(recentlyViewedProducts);
        when(recentlyViewedProducts.getRecentlyViewedProductsList()).thenReturn(Collections.emptyList());

        final String expectedPath = ProductListPageServlet.PRODUCTLIST_JSP_PATH;
        this.servlet.doGet(this.request, this.response);

        verify(this.request).getRequestDispatcher(expectedPath);
        verify(this.requestDispatcher).forward(this.request, this.response);
    }

    @Test
    public void testDoGetWithNullRequestDispatcher() throws ServletException, IOException {
        when(request.getSession()).thenReturn(httpSession);

        servlet.doGet(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), anyString());
    }

    @Test
    public void testDoGetHandleForwardException() throws ServletException, IOException {
        when(request.getSession()).thenReturn(httpSession);
        when(request.getRequestDispatcher(anyString())).thenReturn((this.requestDispatcher));
        when(recentlyViewedProductsService.getRecentlyViewedProductsFromStorage(any(HttpSessionRecentlyViewedProductsStorage.class))).thenReturn(recentlyViewedProducts);
        when(recentlyViewedProducts.getRecentlyViewedProductsList()).thenReturn(Collections.emptyList());


        doThrow(new ServletException(FORWARD_EXCEPTION))
                .when(this.requestDispatcher)
                .forward(this.request, this.response);
        this.servlet.doGet(this.request, this.response);
    }

    @Test
    public void testDoGetHandleIOException() throws ServletException, IOException {
        when(request.getSession()).thenReturn(httpSession);
        when(this.request.getRequestDispatcher(anyString()))
                .thenReturn(this.requestDispatcher);
        when(recentlyViewedProductsService.getRecentlyViewedProductsFromStorage(any(HttpSessionRecentlyViewedProductsStorage.class))).thenReturn(recentlyViewedProducts);
        when(recentlyViewedProducts.getRecentlyViewedProductsList()).thenReturn(Collections.emptyList());

        doThrow(new IOException(FORWARD_EXCEPTION))
                .when(this.requestDispatcher)
                .forward(this.request, this.response);
        this.servlet.doGet(this.request, this.response);
    }

    @Test
    public void testDoGetCallRequestDispatcherOnce() throws ServletException, IOException {
        when(request.getSession()).thenReturn(httpSession);
        when(recentlyViewedProductsService.getRecentlyViewedProductsFromStorage(any(HttpSessionRecentlyViewedProductsStorage.class))).thenReturn(recentlyViewedProducts);
        when(recentlyViewedProducts.getRecentlyViewedProductsList()).thenReturn(Collections.emptyList());
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        this.servlet.doGet(this.request, this.response);
        verify(this.request, times(1)).getRequestDispatcher(
                ProductListPageServlet.PRODUCTLIST_JSP_PATH
        );
    }

    @Test
    public void testDoGetWithEmptyProductList() throws ServletException, IOException {
        when(request.getSession()).thenReturn(httpSession);
        when(this.request.getRequestDispatcher(anyString())).thenReturn(this.requestDispatcher);
        when(this.productDao.findProducts(null, null, null)).thenReturn(Collections.emptyList());
        when(recentlyViewedProductsService.getRecentlyViewedProductsFromStorage(any(HttpSessionRecentlyViewedProductsStorage.class))).thenReturn(recentlyViewedProducts);
        when(recentlyViewedProducts.getRecentlyViewedProductsList()).thenReturn(Collections.emptyList());

        this.servlet.doGet(this.request, this.response);

        verify(this.request).setAttribute(ProductListPageServlet.ATTRIBUTE_PRODUCTS, Collections.emptyList());
        verify(this.request).setAttribute(ProductListPageServlet.ATTRIBUTE_RECENTLY_PRODUCTS, Collections.emptyList());
        verify(this.requestDispatcher).forward(this.request, this.response);
    }

    @Test
    public void testDoGetWithNullList() throws ServletException, IOException {
        when(request.getSession()).thenReturn(httpSession);
        when(this.request.getRequestDispatcher(anyString())).thenReturn(this.requestDispatcher);
        when(this.productDao.findProducts(null, null, null)).thenReturn(null);
        when(recentlyViewedProductsService.getRecentlyViewedProductsFromStorage(any(HttpSessionRecentlyViewedProductsStorage.class))).thenReturn(recentlyViewedProducts);
        when(recentlyViewedProducts.getRecentlyViewedProductsList()).thenReturn(Collections.emptyList());

        this.servlet.doGet(this.request, this.response);

        verify(this.request).setAttribute(ProductListPageServlet.ATTRIBUTE_PRODUCTS, null);
        verify(this.request).setAttribute(ProductListPageServlet.ATTRIBUTE_RECENTLY_PRODUCTS, Collections.emptyList());
        verify(this.requestDispatcher).forward(this.request, this.response);
    }
}



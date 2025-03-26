
package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.product.recentlyviewed.LinkedListRecentlyViewedProducts;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProducts;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProductsService;
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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductDetailsPageServletTest {

    private static final String TEST_MESSAGE = "TEST";
    private static final String TEST_BAD_ID = "123";
    private static final String INVALID_ID_PATH = "/aaaa";
    private static final String VALID_ID_PATH = "/1";
    private static final String TEST_QUANTITY = "2";
    private static final Long PRODUCT_ID = 1L;
    private static final int REQUESTED_STOCK = 1;
    private static final int AVAILABLE_STOCK = 5;

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
    @Mock
    private ServletContext servletContext;
    @Mock
    private RecentlyViewedProductsService recentlyViewedProductsService;
    @Mock
    private CartService cartService;
    @Mock
    private HttpSession session;
    @Mock
    private RecentlyViewedProducts recentlyViewedProducts;

    @InjectMocks
    private ProductDetailsPageServlet servlet;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        servlet = new ProductDetailsPageServlet(productDao, cartService, recentlyViewedProductsService, servletContext);
    }


    @Test
    void init_shouldThrowServletExceptionWhenProductDaoIsNull() {
        when(config.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(anyString())).thenReturn(null);

        ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();

        assertThrows(ServletException.class, () -> servlet.init(config), "Expected ServletException to be thrown when ProductDao is null");
    }

    @Test
    void init_shouldThrowServletExceptionWhenCartServiceIsNull() {
        when(config.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(eq(DependenciesServletContextListener.ATTRIBUTE_PRODUCT_DAO))).thenReturn(productDao);
        when(servletContext.getAttribute(eq(DependenciesServletContextListener.ATTRIBUTE_CART_SERVICE))).thenReturn(null);

        ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();

        assertThrows(ServletException.class, () -> servlet.init(config), "Expected ServletException to be thrown when CartService is null");
    }

    @Test
    void init_shouldThrowServletExceptionWhenRecentlyViewedProductsServiceIsNull() {
        when(config.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(eq(DependenciesServletContextListener.ATTRIBUTE_PRODUCT_DAO))).thenReturn(productDao);
        when(servletContext.getAttribute(eq(DependenciesServletContextListener.ATTRIBUTE_CART_SERVICE))).thenReturn(cartService);
        when(servletContext.getAttribute(eq(DependenciesServletContextListener.ATTRIBUTE_RECENTLY_VIEWED_PRODUCTS_SERVICE))).thenReturn(null);

        ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();

        assertThrows(ServletException.class, () -> servlet.init(config), "Expected ServletException to be thrown when RecentlyViewedProductsService is null");
    }

    @Test
    public void doGet_ProductNotFoundException() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/" + TEST_BAD_ID);
        when(productDao.getProduct(anyLong())).thenThrow(new ProductNotFoundException(TEST_MESSAGE));
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(request).setAttribute(ProductDetailsPageServlet.ATTRIBUTE_PRODUCTCODE, TEST_BAD_ID);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void doGet_NumberFormatException() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn(INVALID_ID_PATH);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, ProductDetailsPageServlet.ERROR_INVALID_ID_FORMAT);
    }

    @Test
    public void doGet_forwardsToProductJsp() throws ServletException, IOException {
        Product product = new Product();
        product.setId(PRODUCT_ID);
        when(request.getPathInfo()).thenReturn(VALID_ID_PATH);
        when(productDao.getProduct(PRODUCT_ID)).thenReturn(product);
        when(request.getRequestDispatcher(ProductDetailsPageServlet.PRODUCT_JSP_PATH)).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(session);
        doNothing().when(recentlyViewedProductsService).add(any(),any());

        when(session.getAttribute(anyString())).thenReturn(new Cart());
        when(session.getAttribute(anyString())).thenReturn(new LinkedListRecentlyViewedProducts());

        servlet.doGet(request, response);

        verify(productDao).getProduct(PRODUCT_ID);
        verify(request).setAttribute(ProductDetailsPageServlet.ATTRIBUTE_PRODUCT, product);
        verify(requestDispatcher).forward(request, response);
    }


    @Test
    public void doPost_ProductNotFoundException() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/" + TEST_BAD_ID);
        when(request.getLocale()).thenReturn(Locale.getDefault());
        when(request.getParameter(ProductDetailsPageServlet.PARAMETER_QUANTITY)).thenReturn(TEST_QUANTITY);
        when(productDao.getProduct(anyLong())).thenThrow(new ProductNotFoundException(TEST_MESSAGE));
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(request).setAttribute(ProductDetailsPageServlet.ATTRIBUTE_PRODUCTCODE, TEST_BAD_ID);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void doPost_NumberFormatException() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn(INVALID_ID_PATH);

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, ProductDetailsPageServlet.ERROR_INVALID_ID_FORMAT);
    }

    @Test
    public void doPost_ParseException() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn(VALID_ID_PATH);
        when(request.getParameter(ProductDetailsPageServlet.PARAMETER_QUANTITY)).thenReturn(INVALID_ID_PATH);
        when(request.getLocale()).thenReturn(Locale.getDefault());
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(response).sendRedirect(String.format(ProductDetailsPageServlet.REDIRECT_PRODUCTS_ID_ERROR, PRODUCT_ID, ProductDetailsPageServlet.ERROR_NOT_NUMBER));
    }

    @Test
    public void doPost_OutOfStockException() throws ServletException, IOException, OutOfStockException {
        when(request.getPathInfo()).thenReturn(VALID_ID_PATH);
        when(request.getParameter(ProductDetailsPageServlet.PARAMETER_QUANTITY)).thenReturn(TEST_QUANTITY);
        when(request.getLocale()).thenReturn(Locale.getDefault());
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("");
        Cart cart = new Cart();
        doThrow(new OutOfStockException(new Product(), REQUESTED_STOCK, AVAILABLE_STOCK)).when(cartService).add(any(Cart.class), eq(PRODUCT_ID), anyInt());

        servlet.doPost(request, response);

        verify(response).sendRedirect(String.format(ProductDetailsPageServlet.REDIRECT_PRODUCTS_ID_ERROR, PRODUCT_ID, String.format(ProductDetailsPageServlet.ERROR_OUT_OF_STOCK, AVAILABLE_STOCK)));
    }

    @Test
    public void doPost_RuntimeException() throws ServletException, IOException, OutOfStockException {
        when(request.getPathInfo()).thenReturn(VALID_ID_PATH);
        when(request.getParameter(ProductDetailsPageServlet.PARAMETER_QUANTITY)).thenReturn(TEST_QUANTITY);
        when(request.getLocale()).thenReturn(Locale.getDefault());
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("");

        doThrow(new RuntimeException(TEST_MESSAGE)).when(cartService).add(any(Cart.class), eq(PRODUCT_ID), anyInt());

        servlet.doPost(request, response);

        verify(response).sendRedirect(anyString());
    }

    @Test
    public void doPost_validRequest() throws ServletException, IOException, OutOfStockException {
        when(request.getPathInfo()).thenReturn(VALID_ID_PATH);
        when(request.getParameter(ProductDetailsPageServlet.PARAMETER_QUANTITY)).thenReturn(TEST_QUANTITY);
        when(request.getLocale()).thenReturn(Locale.getDefault());
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(cartService).add(any(Cart.class), eq(PRODUCT_ID), anyInt());
        verify(response).sendRedirect(String.format(ProductDetailsPageServlet.REDIRECT_PRODUCTS_ID_MESSAGE, PRODUCT_ID, ProductDetailsPageServlet.MESSAGE_PRODUCT_TO_CART));
    }
}
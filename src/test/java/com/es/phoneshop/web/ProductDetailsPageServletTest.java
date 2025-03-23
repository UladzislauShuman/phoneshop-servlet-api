package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProductsService;
import com.es.phoneshop.model.product.recentlyviewed.storage.HttpSessionRecentlyViewedProductsStorage;
import com.es.phoneshop.model.product.recentlyviewed.storage.RecentlyViewedProductsStorageFactory;
import com.es.phoneshop.web.listeners.DependenciesServletContextListener;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductDetailsPageServletTest {
    private static final String TEST_MESSAGE = "TEST";
    private static final String TEST_BAD_ID = "123";
    private static final String TEAR_INVALID_ID = "/aaaa";
    private static final String TEST_GOOD_ID = "/1";
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
    private RecentlyViewedProductsStorageFactory recentlyViewedProductsStorageFactory;
    @Mock
    private CartService cartService;

    private ProductDetailsPageServlet servlet;

    @Mock
    HttpSessionRecentlyViewedProductsStorage storage;

    @BeforeEach
    public void setup() throws ServletException {
        MockitoAnnotations.openMocks(this);
        servlet = new ProductDetailsPageServlet(productDao, cartService, recentlyViewedProductsService, recentlyViewedProductsStorageFactory, servletContext);
    }

    @Test
    public void doGet_testDoubleInit() throws ServletException {
        when(config.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(DependenciesServletContextListener.ATTRIBUTE_PRODUCT_DAO)).thenReturn(productDao);
        when(servletContext.getAttribute(DependenciesServletContextListener.ATTRIBUTE_CART_SERVICE)).thenReturn(cartService);
        when(servletContext.getAttribute(DependenciesServletContextListener.ATTRIBUTE_RECENTLY_VIEWED_PRODUCTS_SERVICE)).thenReturn(recentlyViewedProductsService);

        servlet = new ProductDetailsPageServlet();
        servlet.init(config);

        servlet.init(config);

        verify(servletContext, times(1)).getAttribute(DependenciesServletContextListener.ATTRIBUTE_PRODUCT_DAO);
        verify(servletContext, times(1)).getAttribute(DependenciesServletContextListener.ATTRIBUTE_CART_SERVICE);
        verify(servletContext, times(1)).getAttribute(DependenciesServletContextListener.ATTRIBUTE_RECENTLY_VIEWED_PRODUCTS_SERVICE);
    }

    @Test
    public void doGet_testCatchProductNotFoundException() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/" + TEST_BAD_ID);
        when(productDao.getProduct(any())).thenThrow(new ProductNotFoundException(TEST_MESSAGE));
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(response).setStatus(eq(HttpServletResponse.SC_NOT_FOUND));
        verify(request).setAttribute(eq(ProductDetailsPageServlet.ATTRIBUTE_PRODUCTCODE), eq(TEST_BAD_ID));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void doGet_testCatchNumberFormatException() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn(this.TEAR_INVALID_ID);

        servlet.doGet(request,response);

        verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND), eq(ProductDetailsPageServlet.ERROR_INVALID_ID_FORMAT));
    }

    @Test
    public void doGet_testWithGoodId() throws ServletException, IOException {
        Product testProduct = new Product();
        testProduct.setId(PRODUCT_ID);
        when(request.getPathInfo()).thenReturn(this.TEST_GOOD_ID);
        when(productDao.getProduct(eq(PRODUCT_ID))).thenReturn(testProduct);
        when(request.getRequestDispatcher(eq(ProductDetailsPageServlet.PRODUCT_JSP_PATH))).thenReturn(requestDispatcher);
        when(recentlyViewedProductsStorageFactory.create(recentlyViewedProductsService, request)).thenReturn(storage);

        servlet.doGet(request,response);

        verify(request).setAttribute(eq(ProductDetailsPageServlet.ATTRIBUTE_PRODUCT), eq(testProduct));
        verify(requestDispatcher).forward(request, response);
        verify(recentlyViewedProductsStorageFactory).create(recentlyViewedProductsService, request);
        verify(storage).saveRecentlyViewedProducts(any());
    }

    @Test
    public void doPost_testCatchProductNotFoundException() throws ServletException, IOException, OutOfStockException {
        when(request.getPathInfo()).thenReturn("/" + TEST_BAD_ID);
        when(request.getLocale()).thenReturn(Locale.getDefault());
        when(request.getParameter(ProductDetailsPageServlet.PARAMETER_QUANTITY)).thenReturn(TEST_QUANTITY);
        Cart cart = new Cart();
        when(cartService.getCartFromCartStorage(any())).thenReturn(cart);
        doThrow(new ProductNotFoundException(TEST_MESSAGE)).when(cartService).add(any(), anyLong(), anyInt());
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        servlet.doPost(request, response);

        verify(response).setStatus(eq(HttpServletResponse.SC_NOT_FOUND));
        verify(request).setAttribute(eq(ProductDetailsPageServlet.ATTRIBUTE_PRODUCTCODE), eq(TEST_BAD_ID));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void doPost_testCatchNumberFormatException() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn(this.TEAR_INVALID_ID);

        servlet.doPost(request,response);

        verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND), eq(ProductDetailsPageServlet.ERROR_INVALID_ID_FORMAT));
    }

    @Test
    public void doPost_testCatchParseException() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn(TEST_GOOD_ID);
        when(request.getParameter(ProductDetailsPageServlet.PARAMETER_QUANTITY)).thenReturn(TEAR_INVALID_ID);
        when(request.getLocale()).thenReturn(Locale.getDefault());

        servlet.doPost(request,response);

        verify(response).sendRedirect(request.getContextPath() + String.format(ProductDetailsPageServlet.REDIRECT_PRODUCTS_ID_ERROR, PRODUCT_ID, ProductDetailsPageServlet.ERROR_NOT_NUMBER));
    }

    @Test
    public void doPost_testCatchOutOfStockException() throws ServletException, IOException, OutOfStockException {
        when(request.getPathInfo()).thenReturn(TEST_GOOD_ID);
        when(request.getParameter(ProductDetailsPageServlet.PARAMETER_QUANTITY)).thenReturn(TEST_QUANTITY);
        when(request.getLocale()).thenReturn(Locale.getDefault());
        Cart cart = new Cart();
        when(cartService.getCartFromCartStorage(any())).thenReturn(cart);
        doThrow(new OutOfStockException(new Product(), REQUESTED_STOCK, AVAILABLE_STOCK)).when(cartService).add(eq(cart), eq(PRODUCT_ID), eq(Integer.parseInt(TEST_QUANTITY)));

        servlet.doPost(request,response);

        verify(response).sendRedirect(request.getContextPath() + String.format(ProductDetailsPageServlet.REDIRECT_PRODUCTS_ID_ERROR, PRODUCT_ID, String.format(ProductDetailsPageServlet.ERROR_OUT_OF_STOCK, AVAILABLE_STOCK)));
    }

    @Test
    public void doPost_testCatchException() throws ServletException, IOException, OutOfStockException {
        when(request.getPathInfo()).thenReturn(TEST_GOOD_ID);
        when(request.getParameter(ProductDetailsPageServlet.PARAMETER_QUANTITY)).thenReturn(TEST_QUANTITY);
        when(request.getLocale()).thenReturn(Locale.getDefault());
        when(cartService.getCartFromCartStorage(any())).thenReturn(new Cart());
        doThrow(new RuntimeException(TEST_MESSAGE)).when(cartService).add(any(), eq(PRODUCT_ID), eq(Integer.parseInt(TEST_QUANTITY)));

        servlet.doPost(request,response);

        verify(response).sendRedirect(anyString());
    }

    @Test
    public void doPost_testGoodId() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn(TEST_GOOD_ID);
        when(request.getParameter(ProductDetailsPageServlet.PARAMETER_QUANTITY)).thenReturn(TEST_QUANTITY);
        when(request.getLocale()).thenReturn(Locale.getDefault());
        when(cartService.getCartFromCartStorage(any())).thenReturn(new Cart());

        servlet.doPost(request,response);

        verify(response).sendRedirect(request.getContextPath() + String.format(ProductDetailsPageServlet.REDIRECT_PRODUCTS_ID_MESSAGE, PRODUCT_ID, ProductDetailsPageServlet.MESSAGE_PRODUCT_TO_CART));
    }
}
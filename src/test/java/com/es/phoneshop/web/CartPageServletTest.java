package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.cart.OutOfStockException;
import com.es.phoneshop.model.cart.storage.HttpSessionCartReader;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.RequestDispatcher;
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
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartPageServletTest {
    private static final int STOCK_1 = 20;
    private static final int STOCK_2 = 15;
    private static final int STOCK_3 = 10;

    private static final BigDecimal PRICE_1 = new BigDecimal(100);
    private static final BigDecimal PRICE_2 = new BigDecimal(200);
    private static final BigDecimal PRICE_3 = new BigDecimal(300);
    public static final String TEST_CONTEXT_PATH = "smth";

    public static final String PRODUCT_ID_STRING = "1";
    public static final String QUANTITY_STRING = "2";
    public static final String PRODUCT_ID_STRING_1 = "1";
    public static final String PRODUCT_ID_STRING_2 = "2";
    public static final String PRODUCT_ID_STRING_3 = "3";
    public static final String QUANTITY_STRING_1 = "2";
    public static final String QUANTITY_STRING_BAD = "smthBad";
    public static final String QUANTITY_STRING_3 = "1";

    @Mock
    private CartService cartService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private Cart cart;
    @Mock
    private Product product;
    @Mock
    private RequestDispatcher dispatcher;
    @Mock
    private ProductDao productDao;

    @InjectMocks
    private CartPageServlet servlet;

    @BeforeEach
    void setUp() {
        cartService = new DefaultCartService(productDao);
    }

    @Test
    void doGet_success() throws ServletException, IOException {
        when(request.getSession()).thenReturn(session);
        when(HttpSessionCartReader.getCartFromSession(session)).thenReturn(cart);
        when(request.getRequestDispatcher(CartPageServlet.CART_JSP_PATH)).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(dispatcher).forward(request,response);
    }

    @Test
    void doPost_ShouldUpdateCartAndRedirectOnSuccess() throws ServletException, IOException, OutOfStockException {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(CartPageServlet.ATTRIBUTE_CART)).thenReturn(cart);
        when(request.getLocale()).thenReturn(Locale.US);
        when(request.getContextPath()).thenReturn(TEST_CONTEXT_PATH);
        when(request.getParameterValues(CartPageServlet.PARAMETER_PRODUCT_ID)).thenReturn(new String[]{PRODUCT_ID_STRING});
        when(request.getParameterValues(CartPageServlet.PARAMETER_QUANTITY)).thenReturn(new String[]{QUANTITY_STRING});
        when(productDao.getProduct(1L)).thenReturn(product);

        servlet.doPost(request, response);

        verify(response).sendRedirect(eq(TEST_CONTEXT_PATH + "/cart"));
    }

    @Test
    void doPost_hasMistakesInQuantity() throws ServletException, IOException {
        Currency currency = Currency.getInstance("USD");
        Product product1 = new Product( 1L,"sgs", "Samsung Galaxy S", PRICE_1, currency, STOCK_1, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", null);
        Product product2 = new Product( 2L,"sgs2", "Samsung Galaxy S II", PRICE_2, currency, STOCK_2, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg", null);
        Product product3 = new Product( 3L, "sgs3", "Samsung Galaxy S III", PRICE_3, currency, STOCK_3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg", null);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(CartPageServlet.ATTRIBUTE_CART)).thenReturn(cart);
        when(request.getLocale()).thenReturn(Locale.US);
        when(request.getContextPath()).thenReturn(TEST_CONTEXT_PATH);

        when(request.getParameterValues(CartPageServlet.PARAMETER_PRODUCT_ID)).thenReturn(new String[]{PRODUCT_ID_STRING_1, PRODUCT_ID_STRING_2, PRODUCT_ID_STRING_3});
        when(request.getParameterValues(CartPageServlet.PARAMETER_QUANTITY)).thenReturn(new String[]{QUANTITY_STRING_1, QUANTITY_STRING_BAD, QUANTITY_STRING_3});

        when(productDao.getProduct(product1.getId())).thenReturn(product1);
        when(productDao.getProduct(product2.getId())).thenReturn(product2);
        when(productDao.getProduct(product3.getId())).thenReturn(product3);

        servlet.doPost(request, response);

        verify(response).sendRedirect(eq(TEST_CONTEXT_PATH + "/cart"));
    }
}

package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.OutOfStockException;
import com.es.phoneshop.model.cart.storage.HttpSessionCartReader;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.utils.RedirectPathFormater;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddCartItemServletTest {
    public static final String TEST_CONTEXT_PATH = "smth";
    public static final String BAD_QUANTITY_STRING = "smth bad";
    public static final String BAD_PARAMETR = "not number";
    public static final String PRODUCT_ID_STRING = "1";
    public static final String QUANTITY_STRING = "2";
    public static final long PRODUCT_ID_FROM_STRING = 1L;
    public static final int QUANTITY_FROM_STRING = 2;
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

    @InjectMocks
    private AddCartItemServlet servlet;

    @Test
    void doPost_addProductToCart() throws ServletException, IOException, OutOfStockException {
        when(request.getSession()).thenReturn(session);
        when(HttpSessionCartReader.getCartFromSession(session)).thenReturn(cart);
        when(request.getParameter(AddCartItemServlet.PARAMETER_PRODUCT_ID)).thenReturn(PRODUCT_ID_STRING);
        when(request.getParameter(AddCartItemServlet.PARAMETER_QUANTITY)).thenReturn(QUANTITY_STRING);
        when(request.getLocale()).thenReturn(Locale.FRANCE);

        servlet.doPost(request, response);

        verify(cartService).add(cart, PRODUCT_ID_FROM_STRING, QUANTITY_FROM_STRING);
        verify(session).setAttribute(eq(HttpSessionCartReader.CART_SESSION_ATTRIBUTE), eq(cart));

        ArgumentCaptor<String> redirectCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).sendRedirect(redirectCaptor.capture());

        String expectedRedirect = RedirectPathFormater.formatSuccessPath(
                request.getContextPath(),
                AddCartItemServlet.REDIRECT_PLP_MESSAGE, PRODUCT_ID_FROM_STRING,
                AddCartItemServlet.MESSAGE_PRODUCT_DELETED_FROM_CART);
        assertEquals(expectedRedirect, redirectCaptor.getValue());
    }

    @Test
    void doPost_getNumberFormatExceptionToProductId() throws ServletException, IOException {
        when(request.getParameter(any())).thenReturn(BAD_PARAMETR);

        servlet.doPost(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND), eq(AddCartItemServlet.ERROR_INVALID_ID_FORMAT));
    }

    @Test
    void doPost_getNullPointerExceptionToProductId() throws ServletException, IOException {
        when(request.getParameter(any())).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND), eq(AddCartItemServlet.ERROR_INVALID_ID_FORMAT));
    }

    @Test
    void doPost_getParseExceptionToQuantity() throws ServletException, IOException {
        when(request.getParameter(AddCartItemServlet.PARAMETER_PRODUCT_ID)).thenReturn(PRODUCT_ID_STRING);
        when(request.getParameter(AddCartItemServlet.PARAMETER_QUANTITY)).thenReturn(BAD_QUANTITY_STRING);
        when(request.getLocale()).thenReturn(Locale.FRANCE);
        when(request.getContextPath()).thenReturn(TEST_CONTEXT_PATH);

        servlet.doPost(request, response);

        verify(response).sendRedirect(eq(RedirectPathFormater.formatErrorPath(
                request.getContextPath(), AddCartItemServlet.REDIRECT_PLP_ERROR,
                1L, AddCartItemServlet.ERROR_NOT_NUMBER)));
    }
    @Test
    void doPost_getOutOfStockExceptionTo() throws ServletException, IOException, OutOfStockException {
        when(request.getSession()).thenReturn(session);
        when(HttpSessionCartReader.getCartFromSession(session)).thenReturn(cart);
        when(request.getParameter(AddCartItemServlet.PARAMETER_PRODUCT_ID)).thenReturn(PRODUCT_ID_STRING);
        when(request.getParameter(AddCartItemServlet.PARAMETER_QUANTITY)).thenReturn(QUANTITY_STRING);
        when(request.getLocale()).thenReturn(Locale.FRANCE);
        doThrow(new OutOfStockException(product, 0, 0)).when(cartService).add(any(Cart.class),any(Long.class),anyInt());

        servlet.doPost(request, response);


        verify(response).sendRedirect(eq(RedirectPathFormater.formatErrorPath(
                request.getContextPath(), AddCartItemServlet.REDIRECT_PLP_ERROR,
                1L,
                String.format(AddCartItemServlet.ERROR_OUT_OF_STOCK, 0)))
        );
    }
}

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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DeleteCartItemServletTest {
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
    private DeleteCartItemServlet servlet;

    @Test
    void doPost_deleteProductToCart() throws ServletException, IOException, OutOfStockException {
        when(request.getSession()).thenReturn(session);
        when(HttpSessionCartReader.getCartFromSession(session)).thenReturn(cart);
        when(request.getPathInfo()).thenReturn("/1");

        servlet.doPost(request, response);

        verify(cartService).delete(cart, 1L);
        verify(session).setAttribute(eq(HttpSessionCartReader.CART_SESSION_ATTRIBUTE), eq(cart));

        ArgumentCaptor<String> redirectCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).sendRedirect(redirectCaptor.capture());

        String expectedRedirect = RedirectPathFormater.formatSuccessPath(
                request.getContextPath(),
                DeleteCartItemServlet.REDIRECT_CART_MESSAGE,
                DeleteCartItemServlet.MESSAGE_PRODUCT_DELETED_FROM_CART);
        assertEquals(expectedRedirect, redirectCaptor.getValue());
    }

    @Test
    void doPost_getNumberFormatExceptionToProductId() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/asdasd");

        servlet.doPost(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND), eq(DeleteCartItemServlet.ERROR_INVALID_ID_FORMAT));
    }

    @Test
    void doPost_getNullPointerExceptionToProductId() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND), eq(DeleteCartItemServlet.ERROR_INVALID_ID_FORMAT));
    }
}

package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.storage.HttpSessionCartReader;
import com.es.phoneshop.model.product.Product;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MiniCartServletTest {
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

    @InjectMocks
    private MiniCartServlet servlet;

    @Test
    void doGet_getSuccess() throws ServletException, IOException {
        when(request.getSession()).thenReturn(session);
        when(HttpSessionCartReader.getCartFromSession(session)).thenReturn(cart);

        servlet.doGet(request, response);

        verify(request).getRequestDispatcher(eq(MiniCartServlet.MINI_CART_JSP_PATH));
    }

    @Test
    void doGet_handleUnknownException() throws ServletException, IOException {
        when(request.getSession()).thenReturn(session);
        when(HttpSessionCartReader.getCartFromSession(session)).thenReturn(cart);
        when(request.getRequestDispatcher(any())).thenReturn(dispatcher);
        doThrow(new RuntimeException("test")).when(dispatcher).include(any(),any());

        servlet.doGet(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND), eq("test"));
    }
}


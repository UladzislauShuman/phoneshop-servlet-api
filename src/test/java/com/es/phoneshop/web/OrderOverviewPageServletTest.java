package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.OrderDao;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderOverviewPageServletTest {
    @Mock
    private OrderDao orderDao;
    @Mock
    private HttpSession session;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher dispatcher;

    @InjectMocks
    private OrderOverviewPageServlet servlet;

    @Test
    void doGet_success() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("xyz");
        when(orderDao.getOrderBySecureId(eq("yz"))).thenReturn(new Order());
        when(request.getSession()).thenReturn(session);
        doNothing().when(session).setAttribute(anyString(), any(Cart.class));
        when(request.getRequestDispatcher("/WEB-INF/pages/orderOverview.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(dispatcher).forward(request, response);
    }

    @Test
    void doGet_unknownException() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("xyz");
        when(orderDao.getOrderBySecureId(eq("yz"))).thenReturn(new Order());
        when(request.getSession()).thenReturn(session);
        doNothing().when(session).setAttribute(anyString(), any(Cart.class));
        when(request.getRequestDispatcher("/WEB-INF/pages/orderOverview.jsp")).thenReturn(dispatcher);
        doThrow(new RuntimeException("test")).when(dispatcher).forward(request, response);

        servlet.doGet(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND), eq("test"));
    }
}

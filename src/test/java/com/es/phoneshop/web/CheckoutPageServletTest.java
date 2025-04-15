package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.enums.PaymentMethod;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.OrderService;
import com.es.phoneshop.utils.HttpSessionCartReader;
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
import java.util.List;
import java.util.Map;

import static com.es.phoneshop.web.CheckoutPageServlet.ATTRIBUTE_ERRORS;
import static com.es.phoneshop.web.CheckoutPageServlet.CHECKOUT_JSP_PATH;
import static com.es.phoneshop.web.CheckoutPageServlet.ORDER_DELIVERY_ADDRESS;
import static com.es.phoneshop.web.CheckoutPageServlet.ORDER_FIRST_NAME;
import static com.es.phoneshop.web.CheckoutPageServlet.ORDER_LAST_NAME;
import static com.es.phoneshop.web.CheckoutPageServlet.ORDER_PHONE;
import static com.es.phoneshop.web.CheckoutPageServlet.PARAMETER_DELIVERY_DATE;
import static com.es.phoneshop.web.CheckoutPageServlet.PARAMETER_PAYMENT_METHOD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CheckoutPageServletTest {
    @Mock
    private HttpSession session;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher dispatcher;
    @Mock
    private OrderService orderService;
    @Mock
    private Cart cart;
    @Mock
    private Order  order;

    @InjectMocks
    private CheckoutPageServlet servlet;

    @Test
    void doGet_success() throws ServletException, IOException {
        setDoGetMocking();

        servlet.doGet(request, response);

        verify(request).setAttribute(anyString(), any(Order.class));
        verify(request).setAttribute(anyString(), anyList());
        verify(dispatcher).forward(request,response);
    }

    private void setDoGetMocking() {
        when(request.getSession()).thenReturn(session);
        when(HttpSessionCartReader.getCartFromSession(session)).thenReturn(cart);
        when(cart.isEmpty()).thenReturn(false);
        when(orderService.getOrder(cart)).thenReturn(new Order());
        when(orderService.getPaymentMethods()).thenReturn(List.of(PaymentMethod.values()));
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void doGet_throwException() throws ServletException, IOException {
        setDoGetMocking();
        doThrow(new RuntimeException("test")).when(dispatcher).forward(request, response);

        servlet.doGet(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND), eq("test"));
    }

    @Test
    void doPost_successAndSomeMistakes() throws ServletException, IOException {
        setDoPostMocking();

        servlet.doPost(request, response);

        verify(request).setAttribute(eq(ATTRIBUTE_ERRORS), any(Map.class));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void doPost_throwException() throws ServletException, IOException {
        setDoPostMocking();
        doThrow(new RuntimeException("test")).when(dispatcher).forward(request, response);

        servlet.doPost(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND), eq("test"));
    }

    private void setDoPostMocking() {
        when(request.getSession()).thenReturn(session);
        when(HttpSessionCartReader.getCartFromSession(session)).thenReturn(cart);
        when(orderService.getOrder(cart)).thenReturn(order);
        when(orderService.getPaymentMethods()).thenReturn(List.of(PaymentMethod.values()));
        when(request.getParameter(ORDER_FIRST_NAME)).thenReturn("");
        when(request.getParameter(ORDER_LAST_NAME)).thenReturn("");
        when(request.getParameter(ORDER_PHONE)).thenReturn("375293773738");
        when(request.getParameter(ORDER_DELIVERY_ADDRESS)).thenReturn("");
        when(request.getParameter(PARAMETER_DELIVERY_DATE)).thenReturn("");
        when(request.getParameter(PARAMETER_PAYMENT_METHOD)).thenReturn(String.valueOf(PaymentMethod.CACHE));
        when(request.getRequestDispatcher(CHECKOUT_JSP_PATH)).thenReturn(dispatcher);
    }
}

package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.storage.HttpSessionCartReader;
import com.es.phoneshop.web.listeners.DependenciesServletContextListener;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class MiniCartServlet extends HttpServlet {
    public static final String ATTRIBUTE_CART = "cart";

    public static final String MINI_CART_JSP_PATH = "/WEB-INF/pages/minicart.jsp";

    private static final String SERVLET_EXCEPTION_CART_SERVICE_NULL = "PLP: CartService == null";

    private CartService cartService;

    public MiniCartServlet(CartService cartService) {
        this.cartService = cartService;
    }

    public MiniCartServlet() {
        this.cartService = null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();
        cartService = (CartService) context.getAttribute(DependenciesServletContextListener.ATTRIBUTE_CART_SERVICE);
        throwIfNullAttributes();
    }

    private void throwIfNullAttributes() throws ServletException {
        if (cartService == null) {
            throw new ServletException(SERVLET_EXCEPTION_CART_SERVICE_NULL);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            setAttributesToRequest(request);
            request.getRequestDispatcher(MINI_CART_JSP_PATH).include(request, response);
        } catch (Exception e) {
            handleDoGetExceptions(e, response);
        }
    }

    private void setAttributesToRequest(HttpServletRequest request) {
        Cart cart = HttpSessionCartReader.getCartFromSession(request.getSession());
        request.setAttribute(ATTRIBUTE_CART, cart);
    }

    private void handleDoGetExceptions(Exception exception, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
    }
}

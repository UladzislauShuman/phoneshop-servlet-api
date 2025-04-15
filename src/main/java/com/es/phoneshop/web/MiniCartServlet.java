package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.utils.HttpSessionCartReader;
import com.es.phoneshop.utils.LoggerHelper;
import com.es.phoneshop.web.listeners.DependenciesServletContextListener;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MiniCartServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(MiniCartServlet.class);

    public static final String ATTRIBUTE_CART = "cart";

    public static final String MINI_CART_JSP_PATH = "/WEB-INF/pages/minicart.jsp";

    private static final String SERVLET_EXCEPTION_CART_SERVICE_NULL = "PLP: CartService == null";
    public static final String LOGGER_READDRESS_TO_DO_GET = "readdress to doGet";

    private CartService cartService;

    public MiniCartServlet(CartService cartService) {
        this.cartService = cartService;
    }

    public MiniCartServlet() {
        this.cartService = null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        LoggerHelper.logInit(logger, LoggerHelper.BEGIN);
        super.init(config);
        ServletContext context = config.getServletContext();
        cartService = (CartService) context.getAttribute(DependenciesServletContextListener.ATTRIBUTE_CART_SERVICE);
        throwIfNullAttributes();
        LoggerHelper.logInit(logger, LoggerHelper.SUCCESS);
    }

    private void throwIfNullAttributes() throws ServletException {
        if (cartService == null) {
            LoggerHelper.logInit(logger, SERVLET_EXCEPTION_CART_SERVICE_NULL);
            throw new ServletException(SERVLET_EXCEPTION_CART_SERVICE_NULL);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            LoggerHelper.logDoGet(logger, LoggerHelper.BEGIN);
            setAttributesToRequest(request);
            request.getRequestDispatcher(MINI_CART_JSP_PATH).include(request, response);
            LoggerHelper.logDoGet(logger, LoggerHelper.SUCCESS);
        } catch (Exception e) {
            LoggerHelper.logDoGet(logger, e);
            handleExceptions(e, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            LoggerHelper.logDoPost(logger, LoggerHelper.BEGIN);
            LoggerHelper.logDoPost(logger, LOGGER_READDRESS_TO_DO_GET);
            doGet(request, response);
            LoggerHelper.logDoPost(logger, LoggerHelper.SUCCESS);
        } catch (Exception e) {
            LoggerHelper.logDoPost(logger, e);
            handleExceptions(e, response);
        }
    }

    private void setAttributesToRequest(HttpServletRequest request) {
        Cart cart = HttpSessionCartReader.getCartFromSession(request.getSession());
        LoggerHelper.logDoGet(logger, cart.toString());
        request.setAttribute(ATTRIBUTE_CART, cart);
    }

    private void handleExceptions(Exception exception, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
    }
}

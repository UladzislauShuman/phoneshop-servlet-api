package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.utils.HttpSessionCartReader;
import com.es.phoneshop.utils.LoggerHelper;
import com.es.phoneshop.utils.RedirectPathFormater;
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

public class DeleteCartItemServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(DeleteCartItemServlet.class);

    public static final String ERROR_INVALID_ID_FORMAT = "Invalid product ID format";
    public static final String ERROR_UNIDENTIFIED_EXCEPTION = "Unidentified Exception: %s \n %s";

    public static final String MESSAGE_PRODUCT_DELETED_FROM_CART = "Product deleted from cart";

    public static final String REDIRECT_CART_ERROR = "/cart?error=%s";
    public static final String REDIRECT_CART_MESSAGE = "/cart?message=%s";

    private static final String SERVLET_EXCEPTION_CART_SERVICE_NULL = "PLP: CartService == null";


    private CartService cartService;

    public DeleteCartItemServlet(CartService cartService) {
        this.cartService = cartService;
    }

    public DeleteCartItemServlet() {
        this.cartService = null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        LoggerHelper.logInit(logger,LoggerHelper.BEGIN);
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

    private void handleNumberFormatAndNullPointerExceptions(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, ERROR_INVALID_ID_FORMAT);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LoggerHelper.logDoPost(logger, LoggerHelper.BEGIN);
        Long productId = -1L;
        try {
            productId = parseProductId(request);
            deleteFromCart(request, productId);
            LoggerHelper.logDoPost(logger, "here");
            response.sendRedirect(RedirectPathFormater.formatSuccessPath(request.getContextPath(), REDIRECT_CART_MESSAGE, MESSAGE_PRODUCT_DELETED_FROM_CART));
            LoggerHelper.logDoPost(logger, LoggerHelper.SUCCESS);
        } catch (NumberFormatException | NullPointerException e) {
            LoggerHelper.logDoPost(logger, e);
            handleNumberFormatAndNullPointerExceptions(response);
        } catch (Exception e) {
            LoggerHelper.logDoPost(logger, e);
            handleUnknownException(e, request, response);
        }
    }

    private void handleUnknownException(Exception exception, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(RedirectPathFormater.formatErrorPath(request.getContextPath(), REDIRECT_CART_ERROR,
                String.format(ERROR_UNIDENTIFIED_EXCEPTION, exception.getMessage(), exception.getStackTrace().toString())));
    }

    private Long parseProductId(HttpServletRequest request) throws NumberFormatException {
        String productId = request.getPathInfo().substring(1);
        return Long.valueOf(productId);
    }

    private void deleteFromCart(HttpServletRequest request, Long productId) {
        Cart cart = HttpSessionCartReader.getCartFromSession(request.getSession());
        cartService.delete(cart, productId);
        HttpSessionCartReader.saveCartToSession(request.getSession(), cart);
    }
}

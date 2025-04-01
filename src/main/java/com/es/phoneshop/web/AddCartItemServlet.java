package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.OutOfStockException;
import com.es.phoneshop.model.cart.storage.HttpSessionCartReader;
import com.es.phoneshop.utils.RedirectPathFormater;
import com.es.phoneshop.web.listeners.DependenciesServletContextListener;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class AddCartItemServlet extends HttpServlet {
    public static final String ERROR_INVALID_ID_FORMAT = "Invalid product ID format";
    public static final String ERROR_NOT_NUMBER = "Not a number";
    public static final String ERROR_OUT_OF_STOCK = "Out of Stock, available %d";
    public static final String ERROR_UNIDENTIFIED_EXCEPTION = "Unidentified Exception: %s \n %s";

    public static final String MESSAGE_PRODUCT_DELETED_FROM_CART = "Product added to cart";

    public static final String REDIRECT_PLP_ERROR = "/products?productId=%d&error=%s";
    public static final String REDIRECT_PLP_MESSAGE = "/products?productId=%d&message=%s";

    public static final String SERVLET_EXCEPTION_CART_SERVICE_NULL = "PLP: CartService == null";

    public static final String PARAMETER_PRODUCT_ID = "productId";
    public static final String PARAMETER_QUANTITY = "quantity";

    private CartService cartService;

    public AddCartItemServlet(CartService cartService) {
        this.cartService = cartService;
    }

    public AddCartItemServlet() {
        this.cartService = null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();
        cartService = (CartService) context.getAttribute(DependenciesServletContextListener.ATTRIBUTE_CART_SERVICE);
        throwIfNullAttributes(cartService);
    }

    private void throwIfNullAttributes(CartService cartService) throws ServletException {
        if (cartService == null) {
            throw new ServletException(SERVLET_EXCEPTION_CART_SERVICE_NULL);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long productId = -1L;

        try {
            productId = Long.valueOf(request.getParameter(PARAMETER_PRODUCT_ID));
        } catch (NumberFormatException | NullPointerException e) {
            responseSendError(response, ERROR_INVALID_ID_FORMAT);
        }

        int quantity;
        try {
            quantity = parseQuantity(request.getParameter(PARAMETER_QUANTITY), request.getLocale());
            addOrUpgradeToCart(request, productId, quantity);
            response.sendRedirect(RedirectPathFormater.formatSuccessPath(request.getContextPath(), REDIRECT_PLP_MESSAGE, productId, MESSAGE_PRODUCT_DELETED_FROM_CART));
        } catch (Exception e) {
            handleDoPostException(e, request, response, productId);
        }
    }

    private Integer parseQuantity(String quantity, Locale locale) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(locale);
        return format.parse(quantity).intValue();
    }

    private void addOrUpgradeToCart(HttpServletRequest request, Long productId, int quantity) throws OutOfStockException {
        Cart cart = HttpSessionCartReader.getCartFromSession(request.getSession());
        cartService.add(cart, productId, quantity);
        HttpSessionCartReader.saveCartToSession(request.getSession(), cart);
    }

    private void handleDoPostException(Exception exception, HttpServletRequest request, HttpServletResponse response, Long productId) throws IOException {
        if (exception instanceof NumberFormatException) {
            response.sendRedirect(RedirectPathFormater.formatErrorPath(request.getContextPath(), REDIRECT_PLP_ERROR, productId, ERROR_INVALID_ID_FORMAT));
        } else if (exception instanceof ParseException) {
            response.sendRedirect(RedirectPathFormater.formatErrorPath(request.getContextPath(), REDIRECT_PLP_ERROR, productId, ERROR_NOT_NUMBER));
        } else if (exception instanceof OutOfStockException) {
            response.sendRedirect(RedirectPathFormater.formatErrorPath(request.getContextPath(), REDIRECT_PLP_ERROR, productId,
                    String.format(ERROR_OUT_OF_STOCK, ((OutOfStockException) exception).getStockAvailable())));
        } else {
            response.sendRedirect(RedirectPathFormater.formatErrorPath(request.getContextPath(), REDIRECT_PLP_ERROR, productId,
                    String.format(ERROR_UNIDENTIFIED_EXCEPTION, exception.getMessage(), exception.getStackTrace().toString())));
        }
    }

    private void responseSendError(HttpServletResponse response, String message) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
    }
}

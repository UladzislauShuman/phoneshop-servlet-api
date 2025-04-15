package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.exceptions.OutOfStockException;
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
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CartPageServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CartPageServlet.class);

    public static final String ATTRIBUTE_CART = "cart";
    public static final String ATTRIBUTE_ERRORS = "errors";

    public static final String PARAMETER_PRODUCT_ID = "productId";
    public static final String PARAMETER_QUANTITY = "quantity";

    public static final String ERROR_NOT_NUMBER = "Not a number";
    public static final String ERROR_OUT_OF_STOCK = "Out of Stock, available %d";
    public static final String ERROR_UNIDENTIFIED_EXCEPTION = "Unidentified Exception: %s \n %s";

    public static final String MESSAGE_PRODUCT_TO_CART = "Product added to cart";

    public static final String REDIRECT_CART_ERROR = "/cart?error=%s";
    public static final String REDIRECT_CART_MESSAGE = "/cart?message=%s";
    private static final String REDIRECT_CART_PATH = "/cart";

    public static final String CART_JSP_PATH = "/WEB-INF/pages/cart.jsp";

    private static final String SERVLET_EXCEPTION_CART_SERVICE_NULL = "PLP: CartService == null";
    public static final String NULL_PRODUCT_IDS = "productIds";
    public static final String MESSAGE_NOTHING_TO_UPDATE = "Nothing to update";
    public static final String MESSAGE_S = "message = %s";

    private CartService cartService;

    public CartPageServlet(CartService cartService) {
        this.cartService = cartService;
    }

    public CartPageServlet() {
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

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            LoggerHelper.logDoGet(logger, LoggerHelper.BEGIN);
            setAttributesToRequest(request);
            request.getRequestDispatcher(CART_JSP_PATH).forward(request, response);
            LoggerHelper.logDoGet(logger, LoggerHelper.SUCCESS);
        } catch (Exception e) {
            LoggerHelper.logDoGet(logger, e);
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LoggerHelper.logDoPost(logger, LoggerHelper.BEGIN);
        Map<Long, String> errors = new HashMap<>();
        try {
            String[] productIds = request.getParameterValues(PARAMETER_PRODUCT_ID);
            String[] quantities = request.getParameterValues(PARAMETER_QUANTITY);

            updateToCartAndFindErrors(request, productIds, quantities, errors);
            redirectRequest(request, response, errors);
            LoggerHelper.logDoPost(logger, LoggerHelper.SUCCESS);
        } catch (Exception e) {
            LoggerHelper.logDoPost(logger, e);
            handleDoPostException(e, request, response);
        }
    }

    private void updateToCartAndFindErrors(HttpServletRequest request, String[] productIds, String[] quantities, Map<Long, String> errors) {
        Locale locale = request.getLocale();
        int quantity;
        Long productId = 0L;

        for (int i = 0; i < productIds.length; ++i) {
            try {
                productId = Long.valueOf(productIds[i]);
                quantity = parseQuantity(quantities[i], locale);
                updateToCart(request, productId, quantity);
            } catch (Exception e) {
                LoggerHelper.logDoPost(logger, "putToErrors : " + e.getMessage());
                putToErrors(errors, productId, e);
            }
        }
    }

    private void redirectRequest(HttpServletRequest request, HttpServletResponse response, Map<Long, String> errors) throws IOException {
        request.getSession().setAttribute(ATTRIBUTE_ERRORS, errors);
        if (errors.isEmpty()) {
            response.sendRedirect(RedirectPathFormater.formatSuccessPath(request.getContextPath(), REDIRECT_CART_MESSAGE, MESSAGE_PRODUCT_TO_CART));
        } else {
            response.sendRedirect(request.getContextPath() + REDIRECT_CART_PATH);
        }
    }

    private void updateToCart(HttpServletRequest request, Long productId, Integer quantity) throws OutOfStockException {
        Cart cart = HttpSessionCartReader.getCartFromSession(request.getSession());
        cartService.update(cart, productId, quantity);
        HttpSessionCartReader.saveCartToSession(request.getSession(), cart);
    }

    private void putToErrors(Map<Long, String> errors, Long productId, Exception e) {
        String message;
        if (e instanceof NumberFormatException || e instanceof ParseException)
            message = ERROR_NOT_NUMBER;
        else if (e instanceof OutOfStockException)
            message = String.format(ERROR_OUT_OF_STOCK, ((OutOfStockException) e).getStockAvailable());
        else
            message = String.format(ERROR_UNIDENTIFIED_EXCEPTION, e.getMessage(), e.getStackTrace().toString());
        errors.put(productId, message);
    }

    private void handleDoPostException(Exception exception, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String message;
        if (exception instanceof NullPointerException) {
            message = handleNullPointerException((NullPointerException) exception);
        } else {
            message = String.format(ERROR_UNIDENTIFIED_EXCEPTION, exception.getMessage(), exception.getStackTrace().toString());
        }
        LoggerHelper.logDoPost(logger, String.format(MESSAGE_S, message));
        response.sendRedirect(RedirectPathFormater.formatErrorPath(request.getContextPath(), REDIRECT_CART_ERROR,
                message));
    }

    private Integer parseQuantity(String quantity, Locale locale) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(locale);
        return format.parse(quantity).intValue();
    }

    private String handleNullPointerException(NullPointerException e) {
        if (e.getMessage().contains(NULL_PRODUCT_IDS)) {
            return MESSAGE_NOTHING_TO_UPDATE;
        } else {
            return e.getMessage();
        }
    }
}

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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CartPageServlet extends HttpServlet {
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
            request.getRequestDispatcher(CART_JSP_PATH).forward(request, response);
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<Long, String> errors = new HashMap<>();
        try {
            String[] productIds = request.getParameterValues(PARAMETER_PRODUCT_ID);
            String[] quantities = request.getParameterValues(PARAMETER_QUANTITY);

            updateToCartAndFindErrors(request, productIds, quantities, errors);
            redirectRequest(request, response, errors);
        } catch (Exception e) {
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
        response.sendRedirect(RedirectPathFormater.formatErrorPath(request.getContextPath(), REDIRECT_CART_ERROR,
                String.format(ERROR_UNIDENTIFIED_EXCEPTION, exception.getMessage(), exception.getStackTrace().toString())));
    }

    private Integer parseQuantity(String quantity, Locale locale) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(locale);
        return format.parse(quantity).intValue();
    }
}

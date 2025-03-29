package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.OutOfStockException;
import com.es.phoneshop.model.cart.storage.HttpSessionCartReader;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProductsService;
import com.es.phoneshop.web.config.ErrorPageProperties;
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
    public static final String ATTRIBUTE_PRODUCTCODE = "productCode";
    public static final String ATTRIBUTE_CART = "cart";

    public static final String ERROR_INVALID_ID_FORMAT = "Invalid product ID format";
    public static final String ERROR_NOT_NUMBER = "Not a number";
    public static final String ERROR_OUT_OF_STOCK = "Out of Stock, available %d";
    public static final String ERROR_UNIDENTIFIED_EXCEPTION = "Unidentified Exception: %s \n %s";

    public static final String MESSAGE_PRODUCT_TO_CART = "Product added to cart";

    public static final String REDIRECT_CART_ERROR = "/cart?error=%s";
    public static final String REDIRECT_CART_MESSAGE = "/cart?message=%s";
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
        throwIfNullAttributes(cartService);
    }

    private void throwIfNullAttributes(CartService cartService) throws ServletException {
        if (cartService == null) {
            throw new ServletException(SERVLET_EXCEPTION_CART_SERVICE_NULL);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            setAttributesToRequest(request, response);
            request.getRequestDispatcher(CART_JSP_PATH).forward(request, response);
        } catch (Exception e) {
            handleDoGetExceptions(e,request, response);
        }
    }

    private void setAttributesToRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = HttpSessionCartReader.getCartFromSession(request.getSession());
        request.setAttribute(ATTRIBUTE_CART, cart);
    }

    private void handleDoGetExceptions(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            throw e;
        } catch (ProductNotFoundException productNotFoundException) {
            handleProductNotFoundException(request, response);
        } catch (NumberFormatException numberFormatException) {
            handleNumberFormatException(response);
        } catch (Exception exception) {
            handleDoGetUnknownException(response, exception);
        }
    }

    private void handleProductNotFoundException(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        request.setAttribute(ATTRIBUTE_PRODUCTCODE, request.getPathInfo().substring(1));
        request.getRequestDispatcher(ErrorPageProperties.getErrorPagePath(404).toString()).forward(request, response);
    }

    private void handleNumberFormatException(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, ERROR_INVALID_ID_FORMAT);
    }

    private void handleDoGetUnknownException(HttpServletResponse response, Exception e) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long productId_ = -1L;
        Map<Long, String> errors = new HashMap<>();
        try {
            String[] productIds = request.getParameterValues("productId");
            String[] quantities = request.getParameterValues("quantity");

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
            if (errors.isEmpty()) {
                response.sendRedirect(formatSuccessPath(request, MESSAGE_PRODUCT_TO_CART));
            } else {
                request.setAttribute("errors", errors); //todo: почему-то не отображается главное сообщение
                doGet(request, response);
            }
        } catch (NumberFormatException e) {
            handleNumberFormatException(response);
        } catch (Exception e) {
            handleDoPostParseOrOutOfStockException(e, request, response, productId_);
        }
    }

    private void updateToCart(HttpServletRequest request, Long productId, Integer quantity) throws OutOfStockException {
        Cart cart = HttpSessionCartReader.getCartFromSession(request.getSession());
        cartService.update(cart, productId, quantity);
        HttpSessionCartReader.saveCartToSession(request.getSession(), cart);
    }

    private String formatSuccessPath(HttpServletRequest request, String successMessage) {
        return request.getContextPath() +
                String.format(REDIRECT_CART_MESSAGE, successMessage);
    }

    private void putToErrors(Map<Long, String> errors, Long productId, Exception e) {
        String message = null;
        if (e instanceof NumberFormatException || e instanceof ParseException)
            message = ERROR_NOT_NUMBER;
        else if (e instanceof  OutOfStockException)
            message = String.format(ERROR_OUT_OF_STOCK, ((OutOfStockException) e).getStockAvailable());
        else
            message = String.format(ERROR_UNIDENTIFIED_EXCEPTION, e.getMessage(), e.getStackTrace().toString());
        errors.put(productId, message);
    }

    private void handleDoPostParseOrOutOfStockException(Exception e, HttpServletRequest request, HttpServletResponse response, Long productId) throws IOException {
        try {
            throw e;
        } catch (ParseException parseException) {
            response.sendRedirect(formatErrorPath(request, ERROR_NOT_NUMBER));
        } catch (OutOfStockException outOfStockException) {
            response.sendRedirect(formatErrorPath(request, String.format(ERROR_OUT_OF_STOCK, outOfStockException.getStockAvailable())));
        } catch (Exception exception) {
            response.sendRedirect(formatErrorPath(request,String.format(ERROR_UNIDENTIFIED_EXCEPTION, e.getMessage(), e.getStackTrace().toString())));
        }
    }

    private String formatErrorPath(HttpServletRequest request, String errorMessage) {
        return request.getContextPath() +
                String.format(REDIRECT_CART_ERROR, errorMessage);
    }

    private Integer parseQuantity( String quantity, Locale locale) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(locale);
        return format.parse(quantity).intValue();
    }
}

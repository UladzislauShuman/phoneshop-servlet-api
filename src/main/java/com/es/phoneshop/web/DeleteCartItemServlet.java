package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.OutOfStockException;
import com.es.phoneshop.model.cart.storage.HttpSessionCartReader;
import com.es.phoneshop.model.product.Product;
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

public class DeleteCartItemServlet extends HttpServlet {

    public static final String ATTRIBUTE_PRODUCTCODE = "productCode";

    public static final String ERROR_INVALID_ID_FORMAT = "Invalid product ID format";
    public static final String ERROR_NOT_NUMBER = "Not a number";
    public static final String ERROR_OUT_OF_STOCK = "Out of Stock, available %d";
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
        ServletContext context = config.getServletContext();
        cartService = (CartService) context.getAttribute(DependenciesServletContextListener.ATTRIBUTE_CART_SERVICE);
        throwIfNullAttributes(cartService);
    }

    private void throwIfNullAttributes(CartService cartService) throws ServletException {
        if (cartService == null) {
            throw new ServletException(SERVLET_EXCEPTION_CART_SERVICE_NULL);
        }
    }

    private void handleNumberFormatException(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, ERROR_INVALID_ID_FORMAT);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long productId = -1L;
        try {
            productId = parseProductId(request);
            deleteFromCart(request, productId);
            response.sendRedirect(formatSuccessPath(request, MESSAGE_PRODUCT_DELETED_FROM_CART));
        } catch (NumberFormatException e) {
            handleNumberFormatException(response);
        } catch (Exception e) {
            handleDoPostParseOrOutOfStockException(e, request, response, productId);
        }
    }

    private Long parseProductId(HttpServletRequest request) throws NumberFormatException{
        String productId = request.getPathInfo().substring(1);
        return Long.valueOf(productId);
    }

    private void deleteFromCart(HttpServletRequest request, Long productId) throws OutOfStockException {
        Cart cart = HttpSessionCartReader.getCartFromSession(request.getSession());
        cartService.delete(cart, productId);
        HttpSessionCartReader.saveCartToSession(request.getSession(), cart);
    }

    private String formatSuccessPath(HttpServletRequest request, String successMessage) {
        return request.getContextPath() +
                String.format(REDIRECT_CART_MESSAGE, successMessage);
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
}

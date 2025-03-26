package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.OutOfStockException;
import com.es.phoneshop.model.cart.storage.HttpSessionCartReader;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProducts;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProductsService;
import com.es.phoneshop.model.product.recentlyviewed.storage.HttpSessionRVPReader;
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

public class ProductDetailsPageServlet extends HttpServlet {
    public static final String ATTRIBUTE_PRODUCT = "product";
    public static final String ATTRIBUTE_PRODUCTCODE = "productCode";
    public static final String ATTRIBUTE_CART = "cart";

    public static final String ERROR_INVALID_ID_FORMAT = "Invalid product ID format";
    public static final String ERROR_NOT_NUMBER = "Not a number";
    public static final String ERROR_OUT_OF_STOCK = "Out of Stock, available %d";
    public static final String ERROR_UNIDENTIFIED_EXCEPTION = "Unidentified Exception: %s \n %s";

    public static final String MESSAGE_PRODUCT_TO_CART = "Product added to cart";

    public static final String REDIRECT_PRODUCTS_ID_ERROR = "/products/%d?error=%s";
    public static final String REDIRECT_PRODUCTS_ID_MESSAGE = "/products/%d?message=%s";
    public static final String PRODUCT_JSP_PATH = "/WEB-INF/pages/product.jsp";

    public static final String PARAMETER_QUANTITY = "quantity";

    private static final String SERVLET_EXCEPTION_PRODUCT_DAO_NULL = "PLP: ProductDao == null";
    private static final String SERVLET_EXCEPTION_CART_SERVICE_NULL = "PLP: CartService == null";
    private static final String SERVLET_EXCEPTION_RVM_SERVICE_NULL = "PLP: RecentlyViewedProductsService == null";

    private ProductDao productDao;
    private CartService cartService;
    private RecentlyViewedProductsService recentlyViewedProductsService;

    public ProductDetailsPageServlet(ProductDao productDao, CartService cartService, RecentlyViewedProductsService recentlyViewedProductsService, ServletContext servletContext) {
        this.productDao = productDao;
        this.cartService = cartService;
        this.recentlyViewedProductsService = recentlyViewedProductsService;
    }

    public ProductDetailsPageServlet() {
        this.productDao = null;
        this.cartService = null;
        this.recentlyViewedProductsService = null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (this.productDao == null) {
            ServletContext context = config.getServletContext();
            productDao = (ProductDao) context.getAttribute(DependenciesServletContextListener.ATTRIBUTE_PRODUCT_DAO);
            cartService = (CartService) context.getAttribute(DependenciesServletContextListener.ATTRIBUTE_CART_SERVICE);
            recentlyViewedProductsService = (RecentlyViewedProductsService) context.getAttribute(DependenciesServletContextListener.ATTRIBUTE_RECENTLY_VIEWED_PRODUCTS_SERVICE);
            throwIfNullAttributes(productDao, cartService, recentlyViewedProductsService);
        }
    }

    private void throwIfNullAttributes(ProductDao productDao, CartService cartService, RecentlyViewedProductsService recentlyViewedProductsService) throws ServletException {
        if (productDao == null) {
            throw new ServletException(SERVLET_EXCEPTION_PRODUCT_DAO_NULL);
        }
        if (cartService == null) {
            throw new ServletException(SERVLET_EXCEPTION_CART_SERVICE_NULL);
        }
        if (recentlyViewedProductsService == null) {
            throw new ServletException(SERVLET_EXCEPTION_RVM_SERVICE_NULL);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Product product = this.productDao.getProduct(parseProductId(request));
            saveProductToRecentlyViewedProducts(request, product);
            setAttributesToRequest(request, response,product);
            request.getRequestDispatcher(PRODUCT_JSP_PATH).forward(request, response);
        } catch (Exception e) {
            handleDoGetExceptions(e,request, response);
        }
    }

    private Long parseProductId(HttpServletRequest request) throws NumberFormatException{
        String productId = request.getPathInfo().substring(1);
        return Long.valueOf(productId);
    }

    private void saveProductToRecentlyViewedProducts(HttpServletRequest request, Product product) {
        RecentlyViewedProducts recentlyViewedProducts =
                HttpSessionRVPReader.getRecentlyViewProductsFromSession(request.getSession(),recentlyViewedProductsService);
        recentlyViewedProductsService.add(recentlyViewedProducts, product);
        HttpSessionRVPReader.saveRecentlyViewedProducts(request.getSession(), recentlyViewedProducts);
    }

    private void setAttributesToRequest(HttpServletRequest request, HttpServletResponse response,Product product) throws ServletException, IOException {
        request.setAttribute(ATTRIBUTE_PRODUCT, product);

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
        Long productId = -1L;
        try {
            productId = parseProductId(request);
            Integer quantity = parseQuantity(request);
            addToCart(request, productId, quantity);
            response.sendRedirect(formatSuccessPath(request, productId, MESSAGE_PRODUCT_TO_CART));
        } catch (ProductNotFoundException e) {
            handleProductNotFoundException(request, response);
        } catch (NumberFormatException e) {
            handleNumberFormatException(response);
        } catch (Exception e) {
            handleDoPostParseOrOutOfStockException(e, request, response, productId);
        }
    }

    private void addToCart(HttpServletRequest request, Long productId, Integer quantity) throws OutOfStockException {
        Cart cart = HttpSessionCartReader.getCartFromSession(request.getSession());
        cartService.add(cart, productId, quantity);
        HttpSessionCartReader.saveCartToSession(request.getSession(), cart);
    }

    private String formatSuccessPath(HttpServletRequest request, Long productId, String successMessage) {
        return request.getContextPath() +
                String.format(REDIRECT_PRODUCTS_ID_MESSAGE, productId, successMessage);
    }

    private void handleDoPostParseOrOutOfStockException(Exception e, HttpServletRequest request, HttpServletResponse response, Long productId) throws IOException {
        try {
            throw e;
        } catch (ParseException parseException) {
            response.sendRedirect(formatErrorPath(request,productId, ERROR_NOT_NUMBER));
        } catch (OutOfStockException outOfStockException) {
            response.sendRedirect(formatErrorPath(request,productId, String.format(ERROR_OUT_OF_STOCK, outOfStockException.getStockAvailable())));
        } catch (Exception exception) {
            response.sendRedirect(formatErrorPath(request,productId,String.format(ERROR_UNIDENTIFIED_EXCEPTION, e.getMessage(), e.getStackTrace().toString())));
        }
    }

    private String formatErrorPath(HttpServletRequest request, Long productId, String errorMessage) {
        return request.getContextPath() +
                String.format(REDIRECT_PRODUCTS_ID_ERROR, productId, errorMessage);
    }

    private Integer parseQuantity(HttpServletRequest request) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(request.getLocale());
        return format.parse(request.getParameter(PARAMETER_QUANTITY)).intValue();
    }
}

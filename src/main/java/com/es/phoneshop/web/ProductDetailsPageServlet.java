package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.exceptions.OutOfStockException;
import com.es.phoneshop.utils.HttpSessionCartReader;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.recentlyviewed.RecentlyViewedProducts;
import com.es.phoneshop.model.recentlyviewed.RecentlyViewedProductsService;
import com.es.phoneshop.utils.HttpSessionRVPReader;
import com.es.phoneshop.utils.LoggerHelper;
import com.es.phoneshop.utils.RedirectPathFormater;
import com.es.phoneshop.web.config.ErrorPageProperties;
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

public class ProductDetailsPageServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ProductDetailsPageServlet.class);

    public static final String ATTRIBUTE_PRODUCT = "product";
    public static final String ATTRIBUTE_PRODUCT_CODE = "productCode";
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

    private static final int PRODUCT_NOT_FOUND_EXCEPTION_ERROR_NUMBER = 404;
    public static final String PATH_INFO_IS_EMPTY_OR_NULL = "Path info is empty or null";

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
        LoggerHelper.logInit(logger, LoggerHelper.BEGIN);
        if (this.productDao == null) {
            ServletContext context = config.getServletContext();
            productDao = (ProductDao) context.getAttribute(DependenciesServletContextListener.ATTRIBUTE_PRODUCT_DAO);
            cartService = (CartService) context.getAttribute(DependenciesServletContextListener.ATTRIBUTE_CART_SERVICE);
            recentlyViewedProductsService = (RecentlyViewedProductsService) context.getAttribute(DependenciesServletContextListener.ATTRIBUTE_RECENTLY_VIEWED_PRODUCTS_SERVICE);
            throwIfNullAttributes();
            LoggerHelper.logInit(logger, LoggerHelper.SUCCESS);
        }
    }

    private void throwIfNullAttributes() throws ServletException {
        if (productDao == null) {
            LoggerHelper.logInit(logger, SERVLET_EXCEPTION_PRODUCT_DAO_NULL);
            throw new ServletException(SERVLET_EXCEPTION_PRODUCT_DAO_NULL);
        }
        if (cartService == null) {
            LoggerHelper.logInit(logger, SERVLET_EXCEPTION_CART_SERVICE_NULL);
            throw new ServletException(SERVLET_EXCEPTION_CART_SERVICE_NULL);
        }
        if (recentlyViewedProductsService == null) {
            LoggerHelper.logInit(logger, SERVLET_EXCEPTION_RVM_SERVICE_NULL);
            throw new ServletException(SERVLET_EXCEPTION_RVM_SERVICE_NULL);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            LoggerHelper.logDoGet(logger, LoggerHelper.BEGIN);
            Product product = this.productDao.getProduct(parseProductId(request));
            saveProductToRecentlyViewedProducts(request, product);
            setAttributesToRequest(request, product);
            request.getRequestDispatcher(PRODUCT_JSP_PATH).forward(request, response);
            LoggerHelper.logDoGet(logger, LoggerHelper.SUCCESS);
        } catch (Exception e) {
            LoggerHelper.logDoGet(logger, e);
            handleDoGetExceptions(e, request, response);
        }
    }

    private void saveProductToRecentlyViewedProducts(HttpServletRequest request, Product product) {
        RecentlyViewedProducts recentlyViewedProducts =
                HttpSessionRVPReader.getRecentlyViewProductsFromSession(request.getSession(), recentlyViewedProductsService);
        recentlyViewedProductsService.add(recentlyViewedProducts, product);
        HttpSessionRVPReader.saveRecentlyViewedProducts(request.getSession(), recentlyViewedProducts);
    }

    private void setAttributesToRequest(HttpServletRequest request, Product product){
        request.setAttribute(ATTRIBUTE_PRODUCT, product);
        Cart cart = HttpSessionCartReader.getCartFromSession(request.getSession());
        request.setAttribute(ATTRIBUTE_CART, cart);
    }

    private void handleDoGetExceptions(Exception exception, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (exception instanceof ProductNotFoundException | exception instanceof NumberFormatException) {
            handleProductNotFoundOrNumberFormatException(exception, request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LoggerHelper.logDoPost(logger, LoggerHelper.BEGIN);
        Long productId = -1L;
        try {
            productId = parseProductId(request);
            Integer quantity = parseQuantity(request);
            addToCart(request, productId, quantity);
            response.sendRedirect(RedirectPathFormater.formatSuccessPath(request.getContextPath(), REDIRECT_PRODUCTS_ID_MESSAGE, productId, MESSAGE_PRODUCT_TO_CART));
            LoggerHelper.logDoPost(logger, LoggerHelper.SUCCESS);
        } catch (ProductNotFoundException | NumberFormatException e) {
            LoggerHelper.logDoPost(logger, e);
            handleProductNotFoundOrNumberFormatException(e, request, response);
        } catch (Exception e) {
            LoggerHelper.logDoPost(logger, e);
            handleDoPostParseOrOutOfStockException(e, request, response, productId);
        }
    }

    private void handleProductNotFoundOrNumberFormatException(Exception exception, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (exception instanceof ProductNotFoundException) {
            handleProductNotFoundException(request, response);
        } else if (exception instanceof NumberFormatException) {
            handleNumberFormatException(response);
        }
    }

    private void handleProductNotFoundException(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        String pathInfo = request.getPathInfo();
        if (!(pathInfo == null || pathInfo.isEmpty())) {
            request.setAttribute(ATTRIBUTE_PRODUCT_CODE, pathInfo.substring(1));
            request.getRequestDispatcher(ErrorPageProperties.getErrorPagePath(PRODUCT_NOT_FOUND_EXCEPTION_ERROR_NUMBER)
                    .toString()).forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, PATH_INFO_IS_EMPTY_OR_NULL);
        }
    }

    private void handleNumberFormatException(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, ERROR_INVALID_ID_FORMAT);
    }

    private void handleDoPostParseOrOutOfStockException(Exception exception, HttpServletRequest request, HttpServletResponse response,
                                                        Long productId) throws IOException {
        if (exception instanceof ParseException) {
            response.sendRedirect(RedirectPathFormater.formatErrorPath(request.getContextPath(), REDIRECT_PRODUCTS_ID_ERROR,
                    productId, ERROR_NOT_NUMBER));
        } else if (exception instanceof OutOfStockException) {
            response.sendRedirect(RedirectPathFormater.formatErrorPath(request.getContextPath(), REDIRECT_PRODUCTS_ID_ERROR,
                    productId, String.format(ERROR_OUT_OF_STOCK, ((OutOfStockException) exception).getStockAvailable())));
        } else {
            response.sendRedirect(RedirectPathFormater.formatErrorPath(request.getContextPath(), REDIRECT_PRODUCTS_ID_ERROR, productId,
                    String.format(ERROR_UNIDENTIFIED_EXCEPTION, exception.getMessage(), exception.getStackTrace().toString())));
        }
    }

    private void addToCart(HttpServletRequest request, Long productId, Integer quantity) throws OutOfStockException {
        Cart cart = HttpSessionCartReader.getCartFromSession(request.getSession());
        cartService.add(cart, productId, quantity);
        HttpSessionCartReader.saveCartToSession(request.getSession(), cart);
    }

    private Integer parseQuantity(HttpServletRequest request) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(request.getLocale());
        return format.parse(request.getParameter(PARAMETER_QUANTITY)).intValue();
    }

    private Long parseProductId(HttpServletRequest request) throws NumberFormatException {
        String productId = request.getPathInfo().substring(1);
        return Long.valueOf(productId);
    }
}

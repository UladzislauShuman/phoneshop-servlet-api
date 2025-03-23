package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.OutOfStockException;
import com.es.phoneshop.model.cart.storage.HttpSessionCartStorage;
import com.es.phoneshop.model.product.*;
import com.es.phoneshop.model.product.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProducts;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProductsService;
import com.es.phoneshop.model.product.recentlyviewed.storage.HttpSessionRecentlyViewedProductsStorage;
import com.es.phoneshop.model.product.recentlyviewed.storage.HttpSessionRecentlyViewedProductsStorageFactory;
import com.es.phoneshop.model.product.recentlyviewed.storage.RecentlyViewedProductsStorageFactory;
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

    private ProductDao productDao;
    private CartService cartService;
    private RecentlyViewedProductsService recentlyViewedProductsService;
    private RecentlyViewedProductsStorageFactory recentlyViewedProductsStorageFactory;
    private ServletContext servletContext;

    public ProductDetailsPageServlet(ProductDao productDao, CartService cartService, RecentlyViewedProductsService recentlyViewedProductsService, RecentlyViewedProductsStorageFactory recentlyViewedProductsStorageFactory, ServletContext servletContext) {
        this.productDao = productDao;
        this.cartService = cartService;
        this.recentlyViewedProductsService = recentlyViewedProductsService;
        this.recentlyViewedProductsStorageFactory = recentlyViewedProductsStorageFactory;
        this.servletContext = servletContext;
    }

    public ProductDetailsPageServlet() {
        this.servletContext = null;
        this.productDao = null;
        this.cartService = null;
        this.recentlyViewedProductsService = null;
        this.recentlyViewedProductsStorageFactory = null;
    }
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (this.productDao == null) {
            // нужна ли тут обработка на null, если я гарантированно настраиваю всё?
            // да и зачем, если все равно будет вызвана проверка по ходу программы?
            ServletContext context = config.getServletContext();
            productDao = (ProductDao) context.getAttribute(DependenciesServletContextListener.ATTRIBUTE_PRODUCT_DAO);
            cartService = (CartService) context.getAttribute(DependenciesServletContextListener.ATTRIBUTE_CART_SERVICE);
            recentlyViewedProductsService = (RecentlyViewedProductsService) context.getAttribute(DependenciesServletContextListener.ATTRIBUTE_RECENTLY_VIEWED_PRODUCTS_SERVICE);
            recentlyViewedProductsStorageFactory = new HttpSessionRecentlyViewedProductsStorageFactory();
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Product product = this.productDao.getProduct(
                    parseProductId(request)
            );
            HttpSessionRecentlyViewedProductsStorage storage = (HttpSessionRecentlyViewedProductsStorage) recentlyViewedProductsStorageFactory.create(recentlyViewedProductsService,request);
            RecentlyViewedProducts recentlyViewedProducts = recentlyViewedProductsService
                    .getRecentlyViewedProductsFromStorage(storage);
            recentlyViewedProductsService.add(recentlyViewedProducts, product);
            storage.saveRecentlyViewedProducts(recentlyViewedProducts);

            request.setAttribute(ATTRIBUTE_PRODUCT, product);
            request.setAttribute(ATTRIBUTE_CART, cartService.getCartFromCartStorage(new HttpSessionCartStorage(request)));
            request.getRequestDispatcher(PRODUCT_JSP_PATH).forward(request, response);

        } catch (ProductNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute(ATTRIBUTE_PRODUCTCODE, request.getPathInfo().substring(1));
            request.getRequestDispatcher(ErrorPagePath.ERROR_404.toString()).forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, ERROR_INVALID_ID_FORMAT);
        }
    }

    private Long parseProductId(HttpServletRequest request) throws NumberFormatException{
        String productId = request.getPathInfo().substring(1);
        return Long.valueOf(productId);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long productId = -1L;
        try {
            productId = parseProductId(request);
            Integer quantity = parseQuantity(request);
            Cart cart = cartService.getCartFromCartStorage(new HttpSessionCartStorage(request));
            cartService.add(cart, productId, quantity);
            response.sendRedirect(request.getContextPath() +
                    String.format(REDIRECT_PRODUCTS_ID_MESSAGE, productId, MESSAGE_PRODUCT_TO_CART)); // можно и в другое место(смотря)
        } catch (ProductNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute(ATTRIBUTE_PRODUCTCODE, request.getPathInfo().substring(1));
            request.getRequestDispatcher(ErrorPagePath.ERROR_404.toString()).forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, ERROR_INVALID_ID_FORMAT);
        } catch (ParseException e) {
            response.sendRedirect(request.getContextPath() +
                    String.format(REDIRECT_PRODUCTS_ID_ERROR, productId, ERROR_NOT_NUMBER));
        } catch (OutOfStockException e) {
            response.sendRedirect(request.getContextPath() +
                    String.format(REDIRECT_PRODUCTS_ID_ERROR, productId, String.format(ERROR_OUT_OF_STOCK, e.getStockAvailable())));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() +
                    String.format(REDIRECT_PRODUCTS_ID_ERROR, productId, String.format(ERROR_UNIDENTIFIED_EXCEPTION, e.getMessage(), e.getStackTrace().toString())));
        }
    }

    private Integer parseQuantity(HttpServletRequest request) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(request.getLocale());
        return format.parse(request.getParameter(PARAMETER_QUANTITY)).intValue();
    }
}

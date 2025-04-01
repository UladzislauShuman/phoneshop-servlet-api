package com.es.phoneshop.web.listeners;

import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.product.HashMapProductDao;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.recentlyviewed.DefaultRecentlyViewedProductsService;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProductsService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.util.Currency;

public class DependenciesServletContextListener implements ServletContextListener {
    private static final String CURRENCY = "USD";
    public static final String ATTRIBUTE_PRODUCT_DAO = "productDao";
    public static final String ATTRIBUTE_RECENTLY_VIEWED_PRODUCTS_SERVICE = "recentlyViewedProductsService";
    public static final String ATTRIBUTE_CART_SERVICE = "cartService";
    public static final String ATTRIBUTE_CURRENCY = "currency";

    private ProductDao productDao;
    private RecentlyViewedProductsService recentlyViewedProductsService;
    private CartService cartService;
    private DemoDataInitializer dataInitializer;
    private Currency currency;

    public DependenciesServletContextListener() {
        this.currency = Currency.getInstance(CURRENCY);
        this.productDao = HashMapProductDao.getInstance();
        this.recentlyViewedProductsService = new DefaultRecentlyViewedProductsService();
        this.cartService = new DefaultCartService(productDao);
        this.dataInitializer = new DemoDataInitializer(productDao, currency);

    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        this.dataInitializer.initialize(event);

        context.setAttribute(ATTRIBUTE_PRODUCT_DAO, productDao);
        context.setAttribute(ATTRIBUTE_RECENTLY_VIEWED_PRODUCTS_SERVICE, recentlyViewedProductsService);
        context.setAttribute(ATTRIBUTE_CART_SERVICE, cartService);
        context.setAttribute(ATTRIBUTE_CURRENCY, currency);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }
}

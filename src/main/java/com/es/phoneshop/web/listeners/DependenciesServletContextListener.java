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

public class DependenciesServletContextListener implements ServletContextListener {
    public static final String ATTRIBUTE_PRODUCT_DAO = "productDao";
    public static final String ATTRIBUTE_RECENTLY_VIEWED_PRODUCTS_SERVICE = "recentlyViewedProductsService";
    public static final String ATTRIBUTE_CART_SERVICE = "cartService";

    private ProductDao productDao;
    private RecentlyViewedProductsService recentlyViewedProductsService;
    private CartService cartService;
    private DemoDataInitializer dataInitializer;

    public DependenciesServletContextListener() {
        this.productDao = HashMapProductDao.getInstance();
        this.recentlyViewedProductsService = DefaultRecentlyViewedProductsService.getInstance(productDao);
        this.cartService = DefaultCartService.getInstance(productDao);
        this.dataInitializer = new DemoDataInitializer(productDao);
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        this.dataInitializer.initialize(event);

        context.setAttribute(ATTRIBUTE_PRODUCT_DAO, productDao);
        context.setAttribute(ATTRIBUTE_RECENTLY_VIEWED_PRODUCTS_SERVICE, recentlyViewedProductsService);
        context.setAttribute(ATTRIBUTE_CART_SERVICE, cartService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // связь с БД разорвать и пр
        ServletContextListener.super.contextDestroyed(sce);
    }
}

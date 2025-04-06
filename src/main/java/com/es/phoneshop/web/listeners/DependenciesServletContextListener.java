package com.es.phoneshop.web.listeners;

import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.dao.HashMapOrderDao;
import com.es.phoneshop.model.dao.HashMapProductDao;
import com.es.phoneshop.model.order.DefaultOrderService;
import com.es.phoneshop.model.order.OrderDao;
import com.es.phoneshop.model.order.OrderService;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.recentlyviewed.DefaultRecentlyViewedProductsService;
import com.es.phoneshop.model.recentlyviewed.RecentlyViewedProductsService;
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
    public static final String ATTRIBUTE_ORDER_SERVICE = "orderService";
    public static final String ATTRIBUTE_ORDER_DAO = "orderDao";

    private ProductDao productDao;
    private RecentlyViewedProductsService recentlyViewedProductsService;
    private CartService cartService;
    private DemoDataInitializer dataInitializer;
    private Currency currency;
    private OrderDao orderDao;
    private OrderService orderService;

    public DependenciesServletContextListener() {
        this.currency = Currency.getInstance(CURRENCY);
        this.productDao = HashMapProductDao.getInstance();
        this.orderDao = HashMapOrderDao.getInstance();
        this.recentlyViewedProductsService = new DefaultRecentlyViewedProductsService();
        this.cartService = new DefaultCartService(productDao);
        this.dataInitializer = new DemoDataInitializer(productDao, currency);
        this.orderService = new DefaultOrderService(orderDao);
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        this.dataInitializer.initialize(event);

        context.setAttribute(ATTRIBUTE_PRODUCT_DAO, productDao);
        context.setAttribute(ATTRIBUTE_RECENTLY_VIEWED_PRODUCTS_SERVICE, recentlyViewedProductsService);
        context.setAttribute(ATTRIBUTE_CART_SERVICE, cartService);
        context.setAttribute(ATTRIBUTE_CURRENCY, currency);
        context.setAttribute(ATTRIBUTE_ORDER_SERVICE, orderService);
        context.setAttribute(ATTRIBUTE_ORDER_DAO, orderDao);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }
}

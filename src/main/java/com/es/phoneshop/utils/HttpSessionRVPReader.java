package com.es.phoneshop.utils;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.recentlyviewed.DefaultRecentlyViewedProductsService;
import com.es.phoneshop.model.recentlyviewed.RecentlyViewedProducts;
import com.es.phoneshop.model.recentlyviewed.RecentlyViewedProductsService;
import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.List;

public class HttpSessionRVPReader {
    public static final String RECENTLY_VIEWED_SESSION_ATTRIBUTE = DefaultRecentlyViewedProductsService.class.getName() +
            ".recentlyViewedProducts";

    public static RecentlyViewedProducts getRecentlyViewProductsFromSession(HttpSession session, RecentlyViewedProductsService service) {
        synchronized (session) {
            List<Product> products = (List<Product>) session.getAttribute(RECENTLY_VIEWED_SESSION_ATTRIBUTE);
            if (products == null) {
                return service.createRecentlyViewedProducts(Collections.emptyList());
            } else {
                return service.createRecentlyViewedProducts(products);
            }
        }
    }

    public static void saveRecentlyViewedProducts(HttpSession session, RecentlyViewedProducts recentlyViewedProducts) {
        synchronized (session) {
            session.setAttribute(RECENTLY_VIEWED_SESSION_ATTRIBUTE, recentlyViewedProducts.getRecentlyViewedProductsList());
        }
    }
}

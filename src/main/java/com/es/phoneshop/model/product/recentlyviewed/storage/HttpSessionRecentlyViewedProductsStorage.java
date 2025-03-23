package com.es.phoneshop.model.product.recentlyviewed.storage;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.recentlyviewed.DefaultRecentlyViewedProductsService;
import com.es.phoneshop.model.product.recentlyviewed.LinkedListRecentlyViewedProducts;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProducts;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProductsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/*я посчитал, что делать его потокобезопасным не нужно, я его использую в "потокобезопасной среде"*/
public class HttpSessionRecentlyViewedProductsStorage implements RecentlyViewedProductsStorage{
    public static final String RECENTLY_VIEWED_SESSION_ATTRIBUTE = DefaultRecentlyViewedProductsService.class.getName() +
            ".recentlyViewedProducts";

    private final HttpSession session;
    private final RecentlyViewedProductsService service;

    public HttpSessionRecentlyViewedProductsStorage(RecentlyViewedProductsService service, HttpServletRequest request) {
        this.session = request.getSession();
        this.service = service;
    }

    @Override
    public RecentlyViewedProducts getRecentlyViewedProducts() {
        List<Product> products = (List<Product>) session.getAttribute(RECENTLY_VIEWED_SESSION_ATTRIBUTE);
        if (products == null || products.isEmpty()) {
            return service.createRecentlyViewedProducts(Collections.emptyList());
        } else {
            return service.createRecentlyViewedProducts(products);
        }
    }

    @Override
    public void saveRecentlyViewedProducts(RecentlyViewedProducts recentlyViewed) {
        session.setAttribute(RECENTLY_VIEWED_SESSION_ATTRIBUTE, recentlyViewed.getRecentlyViewedProductsList());
    }

}

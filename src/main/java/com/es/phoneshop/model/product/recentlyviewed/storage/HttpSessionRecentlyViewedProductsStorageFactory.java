package com.es.phoneshop.model.product.recentlyviewed.storage;

import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProductsService;
import jakarta.servlet.http.HttpServletRequest;

public class HttpSessionRecentlyViewedProductsStorageFactory implements RecentlyViewedProductsStorageFactory {
    @Override
    public RecentlyViewedProductsStorage create(RecentlyViewedProductsService recentlyViewedProductsService, HttpServletRequest request) {
        return new HttpSessionRecentlyViewedProductsStorage(recentlyViewedProductsService, request);
    }
}
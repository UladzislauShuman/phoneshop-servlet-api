package com.es.phoneshop.model.product.recentlyviewed.storage;

import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProductsService;
import jakarta.servlet.http.HttpServletRequest;

public interface RecentlyViewedProductsStorageFactory {
    RecentlyViewedProductsStorage create(RecentlyViewedProductsService recentlyViewedProductsService, HttpServletRequest request);
}
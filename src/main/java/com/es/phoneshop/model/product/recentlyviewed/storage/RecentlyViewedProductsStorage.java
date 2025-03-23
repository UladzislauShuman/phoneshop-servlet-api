package com.es.phoneshop.model.product.recentlyviewed.storage;

import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProducts;

public interface RecentlyViewedProductsStorage {
    RecentlyViewedProducts getRecentlyViewedProducts();
    void saveRecentlyViewedProducts(RecentlyViewedProducts recentlyViewed);
}

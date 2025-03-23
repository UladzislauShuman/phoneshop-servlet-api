package com.es.phoneshop.model.product.recentlyviewed;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.recentlyviewed.storage.RecentlyViewedProductsStorage;

import java.util.Collection;

public interface RecentlyViewedProductsService {
    RecentlyViewedProducts getRecentlyViewedProductsFromStorage(RecentlyViewedProductsStorage storage);
    void add(RecentlyViewedProducts recentlyViewed, Product product);
    RecentlyViewedProducts createRecentlyViewedProducts(Collection<Product> collection);
}

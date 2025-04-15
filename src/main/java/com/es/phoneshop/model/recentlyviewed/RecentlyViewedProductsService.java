package com.es.phoneshop.model.recentlyviewed;

import com.es.phoneshop.model.product.Product;

import java.util.Collection;

public interface RecentlyViewedProductsService {
    void add(RecentlyViewedProducts recentlyViewed, Product product);
    RecentlyViewedProducts createRecentlyViewedProducts(Collection<Product> collection);
}

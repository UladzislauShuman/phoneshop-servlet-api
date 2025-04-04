package com.es.phoneshop.model.product.recentlyviewed;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultRecentlyViewedProductsService implements RecentlyViewedProductsService {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final ProductDao productDao;

    public DefaultRecentlyViewedProductsService(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public void add(RecentlyViewedProducts recentlyViewedProducts, Product product) {
        lock.writeLock().lock();
        try {
            recentlyViewedProducts.add(product);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public synchronized RecentlyViewedProducts createRecentlyViewedProducts(Collection<Product> products) {
        return new LinkedListRecentlyViewedProducts(products);
    }
}

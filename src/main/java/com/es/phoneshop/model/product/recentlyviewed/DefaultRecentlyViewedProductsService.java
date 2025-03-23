package com.es.phoneshop.model.product.recentlyviewed;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.recentlyviewed.storage.RecentlyViewedProductsStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultRecentlyViewedProductsService implements RecentlyViewedProductsService {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private ProductDao productDao;

    private DefaultRecentlyViewedProductsService(ProductDao productDao) {this.productDao = productDao;}
    private static volatile RecentlyViewedProductsService instance;

    public static RecentlyViewedProductsService getInstance(ProductDao productDao) {
        // если пользователь введёт новый ProductDao, то он все равно не изменит наш класс
        // но получит "ложное восприятие"
        // поэтому хочется как-то ограничить пользователя вообще от DefaultRVS
        if (productDao == null) {
            throw new NullPointerException("ProdcutDao is Null");
        }
        if (instance != null) {
            return instance;
        }
        synchronized (DefaultRecentlyViewedProductsService.class) {
            if (instance == null) {
                instance = new DefaultRecentlyViewedProductsService(productDao);
            }
        }
        return instance;
    }

    @Override
    public  RecentlyViewedProducts getRecentlyViewedProductsFromStorage(RecentlyViewedProductsStorage storage) {
        lock.readLock().lock();
        try {
            if (storage != null) {
                RecentlyViewedProducts recentlyViewedProducts = storage.getRecentlyViewedProducts();
                if (recentlyViewedProducts == null) {
                    recentlyViewedProducts = createRecentlyViewedProducts(Collections.emptyList());
                    storage.saveRecentlyViewedProducts(recentlyViewedProducts);
                }
                return recentlyViewedProducts;
            }
            return createRecentlyViewedProducts(Collections.emptyList());
        } finally {
            lock.readLock().unlock();
        }
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

    // фабричный метод
    @Override
    public synchronized RecentlyViewedProducts createRecentlyViewedProducts(Collection<Product> products) {
        return new LinkedListRecentlyViewedProducts(products);
    }
}

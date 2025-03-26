package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.exceptions.ProductNotFoundException;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultCartService implements CartService {
    private final ProductDao productDao;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public DefaultCartService(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public void add(Cart cart, Long productId, int quantity) throws ProductNotFoundException, OutOfStockException {
        lock.writeLock().lock();
        try {
            Product product = productDao.getProduct(productId);
            if (!isQuantityInStock(cart, product, quantity)) {
                throw new OutOfStockException(product, quantity, getCurrentAvailableStock(cart, product));
            }
            cart.add(new CartItem(product, quantity));
        } finally {
            lock.writeLock().unlock();
        }
    }

    private boolean isQuantityInStock(Cart cart, Product product, int quantity) {
        return (cart.getQuantity(product) + quantity) <= product.getStock();
    }

    private int getCurrentAvailableStock(Cart cart, Product product) {
        return product.getStock() - cart.getQuantity(product);
    }
}

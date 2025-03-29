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
            if (!isQuantityInStockPlusCurrentQuantity(cart, product, quantity)) {
                throw new OutOfStockException(product, quantity, getCurrentAvailableStock(cart, product));
            }
            cart.add(new CartItem(product, quantity));
            recalculateCart(cart);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private boolean isQuantityInStockPlusCurrentQuantity(Cart cart, Product product, int quantity) {
        return (cart.getQuantity(product) + quantity) <= product.getStock();
    }

    private int getCurrentAvailableStock(Cart cart, Product product) {
        return product.getStock() - cart.getQuantity(product);
    }

    @Override
    public void update(Cart cart, Long productId, int quantity) throws OutOfStockException {
        lock.writeLock().lock();
        try {
            Product product = productDao.getProduct(productId);
            if (!isQuantityInStock(product, quantity)) {
                throw new OutOfStockException(product, quantity, product.getStock());
            }
            cart.update(new CartItem(product, quantity));
            recalculateCart(cart);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private boolean isQuantityInStock(Product product, int quantity) {
        return product.getStock() >= quantity;
    }

    @Override
    public void delete(Cart cart, Long productId) {
        lock.writeLock().lock();
        try {
            cart.delete(productId);
            recalculateCart(cart);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void recalculateCart(Cart cart) {
        cart.setTotalQuantity(
                cart.getItems().stream()
                        .mapToInt(CartItem::getQuantity)
                        .sum()
        );
        //todo :аналогично и для  totalCost
    }
}

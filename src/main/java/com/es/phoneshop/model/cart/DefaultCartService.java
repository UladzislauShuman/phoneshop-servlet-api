package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.exceptions.ProductNotFoundException;

import java.math.BigDecimal;
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
        modifyCart(cart, productId, quantity, true);
    }

    @Override
    public void update(Cart cart, Long productId, int quantity) throws OutOfStockException {
        modifyCart(cart, productId, quantity, false);
    }

    private void modifyCart(Cart cart, Long productId, int quantity, boolean isAdd) throws OutOfStockException {
        lock.writeLock().lock();
        try {
            Product product;
            if (isAdd) {
                product = getProductAndCheckQuantityInStockPlusCurrentQuantity(cart, productId, quantity);
                cart.add(new CartItem(product, quantity));
            } else {
                product = getProductAndCheckQuantityInStock(productId, quantity);
                cart.update(new CartItem(product, quantity));
            }
            recalculateCart(cart);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private Product getProductAndCheckQuantityInStockPlusCurrentQuantity(Cart cart, Long productId, int quantity) throws OutOfStockException, ProductNotFoundException {
        Product product = productDao.getProduct(productId);
        if (!isQuantityInStockPlusCurrentQuantity(cart, product, quantity))
            throw new OutOfStockException(product, quantity, getCurrentAvailableStock(cart, product));
        return product;
    }

    private boolean isQuantityInStockPlusCurrentQuantity(Cart cart, Product product, int quantity) {
        return (cart.getQuantity(product) + quantity) <= product.getStock();
    }

    private int getCurrentAvailableStock(Cart cart, Product product) {
        return product.getStock() - cart.getQuantity(product);
    }


    private Product getProductAndCheckQuantityInStock(Long productId, int quantity) throws OutOfStockException, ProductNotFoundException {
        Product product = productDao.getProduct(productId);
        if (!isQuantityInStock(product, quantity)) {
            throw new OutOfStockException(product, quantity, product.getStock());
        }
        return product;
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
        recalculateTotalQuantity(cart);
        recalculateTotalCost(cart);
    }

    private void recalculateTotalQuantity(Cart cart) {
        cart.setTotalQuantity(
                cart.getItems().stream()
                        .mapToInt(CartItem::getQuantity)
                        .sum()
        );
    }

    private void recalculateTotalCost(Cart cart) {
        cart.setTotalCost(
                cart.getItems().stream()
                        .map(item -> item
                                .getProduct()
                                .getPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
    }
}
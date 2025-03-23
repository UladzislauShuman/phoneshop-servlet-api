package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.cart.storage.CartStorage;
import com.es.phoneshop.model.product.HashMapProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.exceptions.ProductNotFoundException;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultCartService implements CartService {
    private ProductDao productDao;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private DefaultCartService(ProductDao productDao) {
        this.productDao = productDao;
    }

    private static volatile CartService instance;

    public static CartService getInstance(ProductDao productDao) {
        // если пользователь введёт новый ProductDao, то он все равно не изменит наш класс
        // но получит "ложное восприятие"
        // поэтому хочется как-то ограничить пользователя вообще от DefaultC
        if (productDao == null) {
            throw new NullPointerException("ProdcutDao is Null");
        }
        if (instance != null) {
            return instance;
        }
        synchronized (DefaultCartService.class) {
            if (instance == null) { // для ситуации, когда два потока одновременно зашли
                instance = new DefaultCartService(productDao);
            }
            return instance;
        }
    }

    @Override
    public Cart getCartFromCartStorage(CartStorage storage) {
        lock.readLock().lock();
        try {
            Cart cart = storage.getCart();
            if (cart == null) {
                cart = new Cart();
                storage.saveCart(cart);
            }
            return cart;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void add(Cart cart, Long productId, int quantity) throws ProductNotFoundException, OutOfStockException {
        lock.writeLock().lock();
        try {
            Product product = productDao.getProduct(productId); // ProductNotFoundException
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

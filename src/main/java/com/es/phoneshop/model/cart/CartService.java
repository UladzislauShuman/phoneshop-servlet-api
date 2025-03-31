package com.es.phoneshop.model.cart;

public interface CartService {
    void add(Cart cart, Long productId, int quantity) throws OutOfStockException;
}

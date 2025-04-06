package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.exceptions.OutOfStockException;

public interface CartService {
    void add(Cart cart, Long productId, int quantity) throws OutOfStockException;
    void update(Cart cart, Long productId, int quantity) throws OutOfStockException;
    void delete(Cart cart, Long productId);
}

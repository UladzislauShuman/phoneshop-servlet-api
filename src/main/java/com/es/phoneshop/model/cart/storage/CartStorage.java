package com.es.phoneshop.model.cart.storage;

import com.es.phoneshop.model.cart.Cart;

public interface CartStorage {
    Cart getCart();
    void saveCart(Cart cart);
}

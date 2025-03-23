package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.Product;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Cart implements Serializable {
    private HashMap<Long, CartItem> items;

    public Cart() {
        this.items = new HashMap<>();
    }

    public List<CartItem> getItems() {
        return items.values().stream().toList();
    } // может просто вернуть Collection?

    public void setItems(List<CartItem> items) {

    }

    public void add(CartItem cartItem) {
        if (cartItem != null) {
            Long productId = cartItem.getProduct().getId();
            CartItem item = items.get(productId);
            if (item == null) {
                items.put(productId, cartItem);
            } else {
                item.setQuantity(
                        item.getQuantity() + cartItem.getQuantity()
                );
            }
        }
    }

    public int getQuantity(Product product) {
        if (product != null) {
            CartItem cartItem = items.get(product.getId());
            return cartItem == null ? 0 : cartItem.getQuantity();
        } else { // или NullPointerException?
            return 0;
        }

    }
    @Override
    public String toString() {
        return "CART[" + items + "]";
    }
}

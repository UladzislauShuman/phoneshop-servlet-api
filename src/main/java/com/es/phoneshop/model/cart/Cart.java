package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.Product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class Cart implements Serializable {
    private HashMap<Long, CartItem> items;
    private int totalQuantity;
    private BigDecimal totalCost;

    public Cart() {
        this.items = new HashMap<>();
        this.totalQuantity = 0;
        this.totalCost = new BigDecimal(0);
    }

    public List<CartItem> getItems() {
        return items.values().stream().toList();
    }

    public void setItems(List<CartItem> items) {}

    //todo: дублирование кода
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

    //todo: дублирование кода
    public void update(CartItem cartItem) {
        if (cartItem != null) {
            Long productId = cartItem.getProduct().getId();
            CartItem item = items.get(productId);

            if (item == null)
                items.put(productId, cartItem);
            else
                item.setQuantity(cartItem.getQuantity());

        }
    }

    public void delete(Long productId) {
        items.remove(productId);
    }

    public int getQuantity(Product product) {
        if (product != null) {
            CartItem cartItem = items.get(product.getId());
            return cartItem == null ? 0 : cartItem.getQuantity();
        } else {
            return 0;
        }

    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    @Override
    public String toString() {
        return "CART[" + items + "]";
    }
}

package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.Product;

import java.io.Serializable;

public class CartItem  implements Serializable, Cloneable {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.quantity = quantity;
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return product.getCode() + ", " + quantity;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        CartItem cloned = (CartItem) super.clone();
        cloned.product = (Product)  this.product.clone();
        return cloned;
    }
}

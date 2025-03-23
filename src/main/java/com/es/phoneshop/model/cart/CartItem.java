package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.Product;

import java.io.Serializable;

public class CartItem  implements Serializable {
    private Product product;
    private int quantity; // int, а не Integer обозначает, что -- "сюда всегда вставляется нужное значени ..."

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
}

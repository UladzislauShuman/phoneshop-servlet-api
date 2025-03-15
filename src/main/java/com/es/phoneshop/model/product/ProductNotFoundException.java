package com.es.phoneshop.model.product;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(String message) {
        super(message);
    }
    public ProductNotFoundException(Long id) {
        super("ProductNotFoundException: Product with id " + id + " not found");
    }
}

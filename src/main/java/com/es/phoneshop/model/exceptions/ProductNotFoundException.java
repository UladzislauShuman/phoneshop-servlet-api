package com.es.phoneshop.model.exceptions;

public class ProductNotFoundException extends ItemNotFoundException{
    public static final String ID_IS_NULL = "id == null";
    public static final String SAVE_NULL_PRODUCT = "save null Product";
    public static final String ID_NOT_FOUND = "Product with id= %d not found";
    public ProductNotFoundException(String message) {
        super(message);
    }
    public ProductNotFoundException(Long id) {
        super("ProductNotFoundException: Product with id " + id + " not found");
    }
}

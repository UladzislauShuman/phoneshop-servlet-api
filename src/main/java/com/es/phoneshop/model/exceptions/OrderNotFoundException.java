package com.es.phoneshop.model.exceptions;

public class OrderNotFoundException extends ItemNotFoundException{
    public static final String ID_IS_NULL = "id == null";
    public static final String SAVE_NULL_ORDER = "save null Product";
    public static final String ID_NOT_FOUND = "Product with id= %d not found";
    
    public OrderNotFoundException(String message) {
        super(message);
    }
    public OrderNotFoundException() {}
}

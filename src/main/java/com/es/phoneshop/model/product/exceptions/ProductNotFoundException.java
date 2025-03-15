package com.es.phoneshop.model.product.exceptions;

public class ProductNotFoundException extends RuntimeException{
    /*
    сообщений у меня не так уж много Пока
    поэтому я пока их оставлю тут
    в случае если оно каким-то образом вырастит, то вынесу в отдельный enum

     */
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

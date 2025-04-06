package com.es.phoneshop.model.product;

import com.es.phoneshop.model.exceptions.ProductNotFoundException;

import java.util.List;

public interface ProductDao {
    Product getProduct(Long id) throws ProductNotFoundException;
    List<Product> findProducts(String query, String sortField, String sortOrder);
    void save(Product product) throws ProductNotFoundException;
    void delete(Long id);
    void clear();
    boolean isEmpty();
}

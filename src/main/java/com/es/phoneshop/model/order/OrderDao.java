package com.es.phoneshop.model.order;

import com.es.phoneshop.model.exceptions.OrderNotFoundException;

public interface OrderDao {
    Order getOrderBySecureId(Long id) throws OrderNotFoundException;
    void save(Order order) throws OrderNotFoundException;
    Order getOrderBySecureId(String secureId) throws OrderNotFoundException;
}

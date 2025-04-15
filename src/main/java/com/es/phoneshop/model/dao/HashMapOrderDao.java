package com.es.phoneshop.model.dao;

import com.es.phoneshop.model.exceptions.OrderNotFoundException;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.OrderDao;

public class HashMapOrderDao extends HashMapItemDao<Order, Long, OrderNotFoundException> implements OrderDao {

    public static final String MESSAGE_ORDER_WITH_ID_NOT_FOUND = "Order with id %d not found";
    public static final String MESSAGE_ORDER_ID_CANNOT_BE_NULL = "Order ID cannot be null.";
    public static final String MESSAGE_CANNOT_SAVE_NULL_ORDER = "Cannot save a null order.";
    private static volatile OrderDao instance;

    public static OrderDao getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (HashMapOrderDao.class) {
            if (instance == null) {
                instance = new HashMapOrderDao();
            }
            return instance;
        }
    }
    private HashMapOrderDao() {
        super();
    }

    @Override
    protected Long generateId(Order order) {
        return order.getId();
    }

    @Override
    protected void setItemId(Order order, Long id) {
        order.setId(id);
    }

    @Override
    protected OrderNotFoundException getNotFoundException(Long id) {
        return new OrderNotFoundException(String.format(MESSAGE_ORDER_WITH_ID_NOT_FOUND, id));
    }

    @Override
    protected String getIdIsNullMessage() {
        return MESSAGE_ORDER_ID_CANNOT_BE_NULL;
    }

    @Override
    protected String getSaveNullItemMessage() {
        return MESSAGE_CANNOT_SAVE_NULL_ORDER;
    }

    @Override
    public Order getOrderBySecureId(Long id) throws OrderNotFoundException {
        this.validateIdNull(id);

        this.lock.readLock().lock();
        try {
            Order order = items.get(id);
            if (order == null) {
                throw getNotFoundException(id);
            }
            return order;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public Order getOrderBySecureId(String secureId) throws OrderNotFoundException {
        this.lock.readLock().lock();
        try {
            return this.items.values().stream()
                    .filter(id -> secureId.equals(id.getSecureId()))
                    .findFirst()
                    .orElseThrow(OrderNotFoundException::new);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    protected void validateIdNull(Long id) throws OrderNotFoundException {
        if (id == null) {
            throw new OrderNotFoundException(getIdIsNullMessage());
        }
    }

    @Override
    protected void validateItemNull(Order item) throws OrderNotFoundException {
        if (item == null) {
            throw new OrderNotFoundException(getSaveNullItemMessage());
        }
    }
}
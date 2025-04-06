package com.es.phoneshop.model.order;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.enums.PaymentMethod;
import com.es.phoneshop.utils.DeepClonerToHashMap;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultOrderService implements OrderService {
    public static final BigDecimal DEFAULT_DELIVERY_COST = new BigDecimal(5);
    private final OrderDao orderDao;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public DefaultOrderService(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    public Order getOrder(Cart cart) {
        this.lock.readLock().lock();
        try {
            Order order = new Order();
            order.setItems(DeepClonerToHashMap.deepCopyOnlyValues(cart.getItemsMap()));
            order.setSubtotal(cart.getTotalCost());
            order.setDeliveryCost(calculateDeliveryCost());
            order.setTotalCost(order.getSubtotal().add(order.getDeliveryCost()));
            order.setTotalQuantity(cart.getTotalQuantity());
            return order;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private BigDecimal calculateDeliveryCost() {
        return DEFAULT_DELIVERY_COST;
    }

    @Override
    public List<PaymentMethod> getPaymentMethods() {
        return Arrays.asList(PaymentMethod.values());
    }

    @Override
    public void placeOrder(Order order) {
        this.lock.writeLock().lock();
        try {
            order.setSecureId(UUID.randomUUID().toString());
            orderDao.save(order);
        } finally {
            this.lock.writeLock().unlock();
        }
    }
}
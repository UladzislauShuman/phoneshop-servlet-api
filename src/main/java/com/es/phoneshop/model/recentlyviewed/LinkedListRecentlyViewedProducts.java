package com.es.phoneshop.model.recentlyviewed;

import com.es.phoneshop.model.product.Product;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LinkedListRecentlyViewedProducts implements RecentlyViewedProducts {
    private static final int CAPACITY = 3;
    private final LinkedList<Product> queue;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public LinkedListRecentlyViewedProducts() {
        this.queue = new LinkedList<>();
    }
    public LinkedListRecentlyViewedProducts(Collection<Product> products) {
        this.queue = new LinkedList<>(products);
    }

    @Override
    public List<Product> getRecentlyViewedProductsList() {
        lock.readLock().lock();
        try {
            return new LinkedList<>(queue);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void add(Product product) {
        lock.writeLock().lock();
        try {
            if (!isProductNull(product))
                addToQueue(product);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private boolean isProductNull(Product product) {
        return product == null;
    }

    private void addToQueue(Product product) {
        queue.remove(product);
        if (isQueueFull())
            queue.poll();
        queue.offer(product);
    }


    private boolean isQueueFull() {
        return queue.size() == CAPACITY;
    }
}

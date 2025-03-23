package com.es.phoneshop.model.product.recentlyviewed;

import com.es.phoneshop.model.product.Product;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/*я посчитал, что делать его потокобезопасным не нужно, я его использую в "потокобезопасной среде"*/
public class LinkedListRecentlyViewedProducts implements RecentlyViewedProducts {
    private static final int capacity = 3;
    private final LinkedList<Product> queue;

    public LinkedListRecentlyViewedProducts() {
        this.queue = new LinkedList<>();
    }
    public LinkedListRecentlyViewedProducts(Collection<Product> products) {
        this.queue = new LinkedList<>(products);
    }

    @Override
    public List<Product> getRecentlyViewedProductsList() {
        return queue;
    }

    @Override
    public void add(Product product) {
        if (!isProductNull(product))
            addToQueue(product);
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
        return queue.size() == capacity;
    }
}

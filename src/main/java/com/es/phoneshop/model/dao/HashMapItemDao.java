package com.es.phoneshop.model.dao;

import com.es.phoneshop.model.exceptions.ItemNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class HashMapItemDao<T, ID, E extends ItemNotFoundException> {
    private Long maxId = 0L;
    protected final Map<ID, T> items;
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();

    protected HashMapItemDao() {
        this.items = new HashMap<>();
    }

    protected abstract ID generateId(T item);
    protected abstract void setItemId(T item, Long id);
    protected abstract E getNotFoundException(ID id);
    protected abstract String getIdIsNullMessage();
    protected abstract String getSaveNullItemMessage();

    public T getItem(ID id) throws ItemNotFoundException {
        validateIdNull(id);

        this.lock.readLock().lock();
        try {
            T item = items.get(id);
            if (item == null) {
                throw getNotFoundException(id);
            }
            return item;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    protected void validateIdNull(ID id) throws ItemNotFoundException {
        if (id == null) {
            throw new ItemNotFoundException(getIdIsNullMessage());
        }
    }

    public void save(T item) throws ItemNotFoundException {
        validateItemNull(item);

        this.lock.writeLock().lock();
        try {
            ID id = generateId(item);
            if (isIdNull(id)) {
                saveNewItem(item);
            } else {
                if (items.containsKey(id)) {
                    items.put(id, item);
                } else {
                    saveNewItem(item);
                }
            }

        } finally {
            this.lock.writeLock().unlock();
        }
    }

    protected void validateItemNull(T item) throws ItemNotFoundException {
        if (item == null) {
            throw new ItemNotFoundException(getSaveNullItemMessage());
        }
    }

    private boolean isIdNull(ID id) {
        return id == null;
    }

    private void saveNewItem(T item) {
        Long nextId = ++this.maxId;
        setItemId(item, nextId);
        ID newId = generateId(item);
        items.put(newId, item);
    }

    public void delete(ID id) {
        this.lock.writeLock().lock();
        try {
            items.remove(id);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void clear() {
        this.lock.writeLock().lock();
        try {
            this.items.clear();
            maxId = 0L;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }
}

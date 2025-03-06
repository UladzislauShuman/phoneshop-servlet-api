package com.es.phoneshop.model.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ArrayListProductDao implements ProductDao {
    private AtomicLong maxId = new AtomicLong(0);  //AtomicLong?
    private List<Product> products;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public ArrayListProductDao() {
        this.products = new ArrayList<>();
        this.saveSampleProducts();
    }

    public ArrayListProductDao(List<Product> products) {
        this.products = new ArrayList<>();
        for (Product product : products) {
            if (product != null) {
                product.setId(this.maxId.incrementAndGet());
                this.products.add(product);
            }
        }
    }


    @Override
    public Product getProduct(Long id) throws  ProductNotFoundException { // или Optional, или NoSuchElementException, или свой Checked-exception
        this.lock.readLock().lock();
        try{
            return this.products.stream()
                    .filter(product -> id.equals(product.getId()))
                    .findAny()
                    .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + "not found")); // может сделать отдельный шаблон?
        }finally {
            this.lock.readLock().unlock();
        }

    }

    @Override
    public List<Product> findProducts() {
        this.lock.readLock().lock();
        try{
            return this.products.stream()
                    .filter(Objects::nonNull) // надо?
                    .filter(this::isProductPriceNonNull) // нужно ли в stream проверять на null?
                    .filter(this::isProductInStock)
                    .collect(Collectors.toList());
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private boolean isProductInStock(Product product) {
        return product.getStock() > 0;
    }
    private boolean isProductPriceNonNull(Product product) {
        return product.getPrice() != null;
    }

    @Override
    public void save(Product product) {
        this.lock.writeLock().lock();
        try {
            Long id = product.getId();
            if (id == null) {
                product.setId(this.maxId.incrementAndGet()); // пока он добавляется как новый
                this.products.add(product);
                return;
            }
            this.products.stream()
                    .filter(product_ -> id.equals(product_.getId()))
                    .findFirst()
                    .ifPresentOrElse(product_ -> { // если найден, то обновлняем
                        product_.setDescription(product.getDescription());
                        product_.setImageUrl(product.getImageUrl());
                        product_.setPrice(product.getPrice());
                        product_.setCurrency(product.getCurrency());
                        product_.setStock(product.getStock());
                        product_.setCode(product.getCode());
                    }, () -> {
                        product.setId(this.maxId.incrementAndGet()); // пока он добавляется как новый
                        this.products.add(product);
                    });
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public void delete(Long id) { // наверное пусть Mockito грузит данные, а JUnit удаляет
        this.lock.writeLock().lock();
        try {
            // if (id != null)  // избыточна
                this.products.removeIf(product -> Objects.equals(id, product.getId())); // безопасен от NullPointerException
                //я уже захотел вызвать this.findProduct, но у меня бы тогда был бы deadlock
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private void saveSampleProducts(){
        Currency usd = Currency.getInstance("USD");
        this.save(new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        this.save(new Product("sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg"));
        this.save(new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg"));
        this.save(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg"));
        this.save(new Product("iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg"));
        this.save(new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg"));
        this.save(new Product("sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg"));
        this.save(new Product("xperiaxz", "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg"));
        this.save(new Product("nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg"));
        this.save(new Product("palmp", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg"));
        this.save(new Product("simc56", "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg"));
        this.save(new Product("simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg"));
        this.save(new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));
    }
}

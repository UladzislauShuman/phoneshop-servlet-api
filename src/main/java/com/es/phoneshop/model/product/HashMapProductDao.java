
package com.es.phoneshop.model.product;

import com.es.phoneshop.model.product.exceptions.ProductNotFoundException;
import org.apache.maven.shared.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HashMapProductDao implements ProductDao {
    private static volatile ProductDao instance;

    public static ProductDao getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (HashMapProductDao.class) {
            if (instance == null) {
                instance = new HashMapProductDao();
            }
            return instance;
        }
    }

    private Long maxId = 0L;
    private final Map<Long, Product> products;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private HashMapProductDao() {
        this.products = new HashMap<>();
    }

    @Override
    public Product getProduct(Long id) throws ProductNotFoundException {
        validateProductIdNull(id);

        this.lock.readLock().lock();
        try {
            Product product = products.get(id);
            if (product == null) {
                throw getProductNotFoundExceptionWithProductId(id);
            }
            return product;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private void validateProductIdNull(Long id) throws ProductNotFoundException {
        if (id == null)
            throw new ProductNotFoundException(ProductNotFoundException.ID_IS_NULL);
    }

    private ProductNotFoundException getProductNotFoundExceptionWithProductId(Long id) {
        return new ProductNotFoundException(String.format(ProductNotFoundException.ID_NOT_FOUND, id));
    }

    @Override
    public List<Product> findProducts(String query, String sortField, String sortOrder) {
        this.lock.readLock().lock();
        try {
            return findProductsInternal(
                    query,
                    sortField == null ? null : SortField.valueOf(sortField.toUpperCase()),
                    sortOrder == null ? null : SortOrder.valueOf(sortOrder.toUpperCase())
            );
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private List<Product> findProductsInternal(String query, SortField sortField, SortOrder sortOrder) {
        List<Product> filteredProducts = getFilteredProducts(query);

        Comparator<Product> comparator = null;

        if (sortField != null && sortOrder != null) {
            comparator = createFieldComparator(sortField, sortOrder);
        } else if (!filteredProducts.isEmpty() && !isQueryEmpty(query)) {
            List<String> searchTerms = extractSearchTerms(query);
            comparator = createMatchRankComparatorForTerms(searchTerms);
        }

        if (comparator != null) {
            return filteredProducts.stream().sorted(comparator).collect(Collectors.toList());
        } else {
            return new ArrayList<>(filteredProducts);
        }
    }

    private List<Product> getFilteredProducts(String query) {
        if (isQueryEmpty(query)) {
            return createFilteredProductsStream().collect(Collectors.toList());
        }

        List<String> searchTerms = extractSearchTerms(query);

        return createFilteredProductsStream()
                .filter(p -> getMatchRank(p, searchTerms) > 0)
                .collect(Collectors.toList());
    }

    private boolean isQueryEmpty(String query) {
        return StringUtils.isEmpty(query);
    }

    private List<String> extractSearchTerms(String fullQuery) {
        if (isQueryEmpty(fullQuery))
            return Collections.emptyList();

        return Arrays.stream(fullQuery.toLowerCase().trim().split("\\s+"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private Stream<Product> createFilteredProductsStream() {
        return this.products.values().stream()
                .filter(Objects::nonNull)
                .filter(this::isProductPriceNonNull)
                .filter(this::isProductInStock);
    }

    private boolean isProductInStock(Product product) {
        return product.getStock() > 0;
    }

    private boolean isProductPriceNonNull(Product product) {
        return product.getPrice() != null;
    }

    private int getMatchRank(Product product, List<String> searchTerms) {
        if (searchTerms.isEmpty()) {
            return 0;
        }

        String description = StringUtils.defaultString(product.getDescription().toLowerCase());

        boolean containsAll = searchTerms.stream().allMatch(term -> containsWord(description, term));
        boolean containsAny = searchTerms.stream().anyMatch(term -> containsWord(description, term));

        if (containsAll) {
            return 2;
        } else if (containsAny) {
            return 1;
        } else {
            return 0;
        }
    }


    private Comparator<Product> createMatchRankComparatorForTerms(List<String> searchTerms) {
        return Comparator
                .comparingInt((Product p) -> getMatchRank(p, searchTerms))
                .reversed();
    }

    private Comparator<Product> createFieldComparator(SortField sortField, SortOrder sortOrder) {
        if (sortField == null) {
            return null;
        }
        Comparator<Product> comparator = null;
        switch (sortField) {
            case PRICE:
                comparator = Comparator.comparing(Product::getPrice, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case DESCRIPTION:
                comparator = Comparator.comparing(Product::getDescription, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
        }
        if (comparator != null && sortOrder == SortOrder.DESC) {
            comparator = comparator.reversed();
        }
        return comparator;
    }

    private boolean containsWord(String description, String word) {
        return Arrays.asList(description.split("\\s+")).stream().anyMatch(descWord -> descWord.contains(word));
    }

    @Override
    public void save(Product product) throws ProductNotFoundException {
        validateProductNull(product);
        this.lock.writeLock().lock();
        try {
            Long id = product.getId();
            if (isProductIdNull(id)) {
                saveProductAsInexisted(product);
            } else {
                if (products.containsKey(id)) {
                    products.put(id, product);
                } else {
                    saveProductAsInexisted(product);
                }
            }

        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private void validateProductNull(Product product) throws ProductNotFoundException {
        if (product == null) {
            throw new ProductNotFoundException(ProductNotFoundException.SAVE_NULL_PRODUCT);
        }
    }

    private boolean isProductIdNull(Long id) {
        return id == null;
    }

    private void saveProductAsInexisted(Product product) {
        product.setId(++this.maxId);
        products.put(product.getId(), product);
    }

    @Override
    public void delete(Long id) {
        this.lock.writeLock().lock();
        try {
            products.remove(id);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public void clear() {
        this.lock.writeLock().lock();
        try {
            this.products.clear();
            maxId = 0L;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        return this.products.isEmpty();
    }
}
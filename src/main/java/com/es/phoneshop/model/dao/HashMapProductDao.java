package com.es.phoneshop.model.dao;

import com.es.phoneshop.model.enums.SortField;
import com.es.phoneshop.model.enums.SortOrder;
import com.es.phoneshop.model.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import org.apache.maven.shared.utils.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HashMapProductDao extends HashMapItemDao<Product, Long, ProductNotFoundException> implements ProductDao {

    public static final String MESSAGE_PRODUCT_WITH_ID_NOT_FOUND = "Product with id %d not found";
    public static final String MESSAGE_PRODUCT_ID_CANNOT_NULL = "Product ID cannot be null.";
    public static final String MESSAGE_CANNOT_SAVE_NULL_PRODUCT = "Cannot save a null product.";
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

    private HashMapProductDao() {
        super();
    }

    @Override
    protected Long generateId(Product product) {
        return product.getId();
    }

    @Override
    protected void setItemId(Product product, Long id) {
        product.setId(id);
    }

    @Override
    protected ProductNotFoundException getNotFoundException(Long id) {
        return new ProductNotFoundException(String.format(MESSAGE_PRODUCT_WITH_ID_NOT_FOUND, id));
    }

    @Override
    protected String getIdIsNullMessage() {
        return MESSAGE_PRODUCT_ID_CANNOT_NULL;
    }

    @Override
    protected String getSaveNullItemMessage() {
        return MESSAGE_CANNOT_SAVE_NULL_PRODUCT;
    }

    @Override
    public Product getProduct(Long id) throws ProductNotFoundException {
        return getItem(id);
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
        return this.items.values().stream()
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
    protected void validateIdNull(Long id) throws ProductNotFoundException {
        if (id == null) {
            throw new ProductNotFoundException(getIdIsNullMessage());
        }
    }

    @Override
    protected void validateItemNull(Product item) throws ProductNotFoundException {
        if (item == null) {
            throw new ProductNotFoundException(getSaveNullItemMessage());
        }
    }

    @Override
    public List<Product> findProducts(String query, BigDecimal minPrice, BigDecimal maxPrice) {
        this.lock.readLock().lock();
        try {
            return findProductsInternal(
                    query,
                    minPrice,
                    maxPrice
            );
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private List<Product> findProductsInternal(String query, BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> filteredProducts = getFilteredProducts(query);
        Predicate<Product> predicate = getMinMaxPricePredicate(minPrice, maxPrice);
        Comparator<Product> comparator = Comparator.comparing(Product::getPrice, Comparator.nullsLast(Comparator.naturalOrder()));

        if (true) {
            return filteredProducts.stream()
                    .filter(predicate)
                    .sorted(comparator)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>(filteredProducts);
        }
    }

    private Predicate<Product> getMinMaxPricePredicate(BigDecimal minPrice, BigDecimal maxPrice) {
        return product -> {
            if (product.getPrice() == null) {
                return false;
            }
            boolean matchesMin = minPrice == null || product.getPrice().compareTo(minPrice) >= 0;
            boolean matchesMax = maxPrice == null || product.getPrice().compareTo(maxPrice) <= 0;
            return matchesMin && matchesMax;
        };
    }
}

package com.es.phoneshop.model.product;

import com.es.phoneshop.model.product.exceptions.ProductNotFoundException;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ArrayListProductDao implements ProductDao {

    private static volatile ProductDao instance;
    public static ProductDao getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (ArrayListProductDao.class) {
            if (instance == null) { // для ситуации, когда два потока одновременно зашли
                instance = new ArrayListProductDao();
            }
            return instance;
        }
    }

    private Long maxId = 0L;  //Long, тк и так потокобезопасен
    private final List<Product> products;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private ArrayListProductDao() {
        this.products = new ArrayList<>();
    }

    @Override
    public Product getProduct(Long id) throws ProductNotFoundException { // или Optional, или NoSuchElementException, или свой Checked-exception
        validateProductIdNull(id);

        this.lock.readLock().lock();
        try{
            return getProductById(id)
                    .orElseThrow(() -> this.getProdcutFoundExceptionWithProductId(id)); // method reference "не вставиться"
        }finally {
            this.lock.readLock().unlock();
        }
    }

    private void validateProductIdNull(Long id) throws ProductNotFoundException {
        if (id == null)
            throw new ProductNotFoundException(ProductNotFoundException.ID_IS_NULL);
    }
    private Optional<Product> getProductById(Long id) {
        return this.products.stream()
                .filter(product -> id.equals((product.getId())))
                .findAny();
    }
    private ProductNotFoundException getProdcutFoundExceptionWithProductId(Long id) {
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
        /*
        // если убрать этот участок кода
        // то всё будет работать, но будет тогда гора лишних вычислений
         */
        if (isQueryEmpty(query))
            return handleEmptyQuery(sortField, sortOrder);

        List<String> searchTerms = extractSearchTerms(query);

        Stream<Product> productStream = createFilteredProductsStream()
                .filter(p -> getMatchRank(p, searchTerms) > 0); // Оставляем только совпадающие товары

        Comparator<Product> comparator = createComparator(query, sortField, sortOrder);

        return productStream.sorted(comparator).collect(Collectors.toList());
    }

    private boolean isQueryEmpty(String query) {
        return StringUtils.isEmpty(query);
    }
    private List<Product> handleEmptyQuery(SortField sortField, SortOrder sortOrder) {
        Stream<Product> productStream = createFilteredProductsStream();
        Comparator<Product> comparator = createFieldComparator(sortField, sortOrder);
        if (comparator != null) {
            return productStream.sorted(comparator).collect(Collectors.toList());
        } else {
            return productStream.collect(Collectors.toList());
        }
    }

    private List<String> extractSearchTerms(String fullQuery) {
        if (isQueryEmpty(fullQuery)) // зачем проверять, если проверили выше? с расчётом на "а мало ли в Другом месте"?
            return Collections.emptyList();

        return Arrays.stream(fullQuery.toLowerCase().trim().split("\\s+"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private Stream<Product> createFilteredProductsStream() {
        return  this.products.stream()
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
            return 1;
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

    private Comparator<Product> createComparator(String query, SortField sortField, SortOrder sortOrder) {
        List<String> searchTerms = extractSearchTerms(query);

        Comparator<Product> matchRankComparator = createMatchRankComparatorForTerms(searchTerms);
        Comparator<Product> fieldComparator = createFieldComparator(sortField, sortOrder);

        return combineComparators(matchRankComparator, fieldComparator);
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
        /*
        пока он достаточно мал, поэтому не целесообразно использовать паттерн проектирования Strategy с методом getComparator
         */
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

    private Comparator<Product> combineComparators(Comparator<Product> matchScoreComparator, Comparator<Product> fieldComparator) {
        if (fieldComparator != null) {
            return matchScoreComparator.thenComparing(fieldComparator);
        } else {
            return matchScoreComparator;
        }
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
                this.getProductById(id)
                        .ifPresentOrElse(product_ -> { // если найден, то обновляем
                            product_.setDescription(product.getDescription());
                            product_.setImageUrl(product.getImageUrl());
                            product_.setPrice(product.getPrice());
                            product_.setCurrency(product.getCurrency());
                            product_.setStock(product.getStock());
                            product_.setCode(product.getCode());
                            product_.setProductHistories(product.getProductHistories());
                        }, () -> {
                            saveProductAsInexisted(product);
                        });
            }

        } finally {
            this.lock.writeLock().unlock();
        }
    }
    private void validateProductNull(Product product) throws ProductNotFoundException{
        if (product == null) {
            throw new ProductNotFoundException(ProductNotFoundException.SAVE_NULL_PRODUCT);
        }
    }

    private boolean isProductIdNull(Long id) {
        return id == null;
    }
    private void saveProductAsInexisted(Product product) { // нужно ли его защищать от потоков, если он только безопасно вызывается?
        product.setId(++this.maxId);
        this.products.add(product);
    }

    @Override
    public void delete(Long id) { // наверное пусть Mockito грузит данные, а JUnit удаляет
        this.lock.writeLock().lock();
        try {
            this.products.removeIf(product -> Objects.equals(id, product.getId())); // безопасен от NullPointerException
            //я уже захотел вызвать this.findProduct, но у меня бы тогда был бы deadlock
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
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        return this.products.isEmpty();
    }
}
package com.es.phoneshop.model.product;

import java.util.*;
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
    public Product getProduct(Long id) throws  ProductNotFoundException { // или Optional, или NoSuchElementException, или свой Checked-exception
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
            throw new ProductNotFoundException("ProductNotFoundException: id == null");
    }
    private Optional<Product> getProductById(Long id) {
        return this.products.stream()
                .filter(product -> id.equals((product.getId())))
                .findAny();
    }
    private ProductNotFoundException getProdcutFoundExceptionWithProductId(Long id) {
        return new ProductNotFoundException(id);
    }

    /*
    как происходит сортировка с не null SortField и SortOrder
        - сортируем По убыванию (на странице это сверху вниз) Количество совпадений по словам
        - потом внутри каждого Количества, сортируем согласно Field и Order
    */

    @Override
    public List<Product> findProducts(String query, String sortField, String sortOrder) {
        this.lock.readLock().lock();
        try {
            return findProductsInternal(
                    query,
                    sortField == null ? null : SortField.valueOf(sortField),
                    sortOrder == null ? null : SortOrder.valueOf(sortOrder)
            );
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private List<Product> findProductsInternal(String query, SortField sortField, SortOrder sortOrder) {
        if (isQueryEmpty(query)) {
            return handleEmptyQuery(sortField, sortOrder);
        }
        Stream<Product> productStream = createFilteredProductsStream();
        Comparator<Product> comparator = createComparator(query, sortField, sortOrder);

        return productStream
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    private boolean isQueryEmpty(String query) {
        return query == null || query.trim().isEmpty();
    }
    private List<Product> handleEmptyQuery(SortField sortField, SortOrder sortOrder) {
        Stream<Product> productStream = createFilteredProductsStream();
        Comparator<Product> comparator = getComparator(sortField, sortOrder);
        if (comparator != null) {
            return productStream.sorted(comparator).collect(Collectors.toList());
        } else {
            return productStream.collect(Collectors.toList());
        }
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
    private Comparator<Product> createComparator(String query, SortField sortField, SortOrder sortOrder) {
        String fullQuery = prepareQuery(query);
        List<String> searchTerms = extractSearchTerms(fullQuery);

        Comparator<Product> matchScoreComparator = createMatchScoreComparator(searchTerms, fullQuery);
        Comparator<Product> fieldComparator = getComparator(sortField, sortOrder);

        return combineComparators(matchScoreComparator, fieldComparator);
    }

    private String prepareQuery(String query) {
        return (query == null) ? "" :  query.toLowerCase().trim();
    }

    private List<String> extractSearchTerms(String fullQuery) {
        return Arrays.stream(fullQuery.split("\\s+"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private Comparator<Product> createMatchScoreComparator(List<String> searchTerms, String fullyQuery) {
        return Comparator
                .comparingInt((Product p) -> getMatchScore(p,searchTerms, fullyQuery))
                .reversed();
    }

    private Comparator<Product> combineComparators(Comparator<Product> matchScoreComparator, Comparator<Product> fieldComparator) {
        if (fieldComparator != null) {
            return matchScoreComparator.thenComparing(fieldComparator);
        } else {
            return matchScoreComparator;
        }
    }
    /*
    есть два множества-с-повторениями
    ищем их пересечение
    и количество элементов в их пересечении и есть MatchScore
    под последовательности (к примеру Nokia и Noki) не рассматриваются
     */
    private int getMatchScore(Product product, List<String> searchTerms, String fullQuery) {
        String description = product.getDescription() == null ? "" : product.getDescription().toLowerCase();

        List<String> queryWords = Arrays.asList(fullQuery.split("\\s+"));
        List<String> descriptionWords = Arrays.asList(description.split("\\s+"));

        int intersectionSize = 0;
        List<String> matchedWords = new ArrayList<>();

        for (String queryWord : queryWords) {
            for (String descriptionWord : descriptionWords) {
                if (queryWord.equals(descriptionWord) && !matchedWords.contains(queryWord)) {
                    intersectionSize++;
                    matchedWords.add(queryWord);
                    break;
                }
            }
        }
        return intersectionSize;
    }


    private Comparator<Product> getComparator(SortField sortField, SortOrder sortOrder) {
        if (sortField == null) {
            return null;
        }
        Comparator<Product> comparator = null;
        /*
        пока он достаточно мал, поэтому не целесообразно использовать паттерн проектирования Strategy с методом getComparator
         */
        switch (sortField) {
            case price:
                comparator = Comparator.comparing(Product::getPrice, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case description:
                comparator = Comparator.comparing(Product::getDescription, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
        }
        if (comparator != null && sortOrder == SortOrder.desc) {
            comparator = comparator.reversed();
        }
        return comparator;
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
            throw new ProductNotFoundException("trying to save null Product");
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
}

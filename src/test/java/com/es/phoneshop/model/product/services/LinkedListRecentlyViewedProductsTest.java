package com.es.phoneshop.model.product.services;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.recentlyviewed.LinkedListRecentlyViewedProducts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LinkedListRecentlyViewedProductsTest {

    private LinkedListRecentlyViewedProducts recentlyViewedProducts;

    private static Product product1;
    private static Product product2;
    private static Product product3;
    private static Product product4;


    @BeforeAll
    static void setUpProducts() {
        product1 = createTestProduct(1L);
        product2 = createTestProduct(2L);
        product3 = createTestProduct(3L);
        product4 = createTestProduct(4L);
    }

    @BeforeEach
    void setUp() {
        recentlyViewedProducts = new LinkedListRecentlyViewedProducts();
    }

    @Test
    void add_validProductToEmptyRVP() {
        Product product = createTestProduct(1L);
        recentlyViewedProducts.add(product);
        assertEquals(1, recentlyViewedProducts.getRecentlyViewedProductsList().size());
        assertEquals(product, recentlyViewedProducts.getRecentlyViewedProductsList().get(0));
    }

    @Test
    void add_nullProduct() {
        int initialSize = recentlyViewedProducts.getRecentlyViewedProductsList().size();
        recentlyViewedProducts.add(null);
        assertEquals(initialSize, recentlyViewedProducts.getRecentlyViewedProductsList().size());
    }

    @Test
    void add_duplicateProduct_staysTheSame() {
        recentlyViewedProducts.add(product1); // 1
        recentlyViewedProducts.add(product2); // 1 2
        recentlyViewedProducts.add(product1); // 2 1

        assertEquals(2, recentlyViewedProducts.getRecentlyViewedProductsList().size());
        assertEquals(product2, recentlyViewedProducts.getRecentlyViewedProductsList().get(0));
        assertEquals(product1, recentlyViewedProducts.getRecentlyViewedProductsList().get(1));
    }

    @Test
    void add_moreThanCapacity() {
        recentlyViewedProducts.add(product1); // 1
        recentlyViewedProducts.add(product2); // 1 2
        recentlyViewedProducts.add(product3); // 1 2 3
        recentlyViewedProducts.add(product4); // 2 3 4

        assertEquals(3, recentlyViewedProducts.getRecentlyViewedProductsList().size());
        assertFalse(recentlyViewedProducts.getRecentlyViewedProductsList().contains(product1));
        assertTrue(recentlyViewedProducts.getRecentlyViewedProductsList().contains(product2));
        assertTrue(recentlyViewedProducts.getRecentlyViewedProductsList().contains(product3));
        assertTrue(recentlyViewedProducts.getRecentlyViewedProductsList().contains(product4));
    }

    @Test
    void constructor_withCollection() {
        List<Product> initialProducts = new ArrayList<>();

        initialProducts.add(product1);
        initialProducts.add(product2);

        LinkedListRecentlyViewedProducts recentlyViewedProductsWithCollection = new LinkedListRecentlyViewedProducts(initialProducts);

        assertEquals(2, recentlyViewedProductsWithCollection.getRecentlyViewedProductsList().size());
        assertEquals(product1, recentlyViewedProductsWithCollection.getRecentlyViewedProductsList().get(0));
        assertEquals(product2, recentlyViewedProductsWithCollection.getRecentlyViewedProductsList().get(1));
    }

    private static Product createTestProduct(Long id) {
        Product product = new Product();
        product.setId(id);
        product.setDescription("Product " + id);
        return product;
    }
}

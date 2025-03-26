package com.es.phoneshop.model.product.services;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.recentlyviewed.DefaultRecentlyViewedProductsService;
import com.es.phoneshop.model.product.recentlyviewed.LinkedListRecentlyViewedProducts;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProducts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DefaultRecentlyViewedProductsServiceTest {
    private static final Long PRODUCT_ID = 1L;
    private static final int PRODUCT_STOCK = 10;

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private DefaultRecentlyViewedProductsService service;

    private RecentlyViewedProducts testRecentlyViewedProducts;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(PRODUCT_ID);
        testProduct.setStock(PRODUCT_STOCK);
        testRecentlyViewedProducts = new LinkedListRecentlyViewedProducts();
    }

    @Test
    void add_productToRecentlyViewedProducts() {
        service.add(testRecentlyViewedProducts, testProduct);

        assertEquals(1, testRecentlyViewedProducts.getRecentlyViewedProductsList().size());
        assertEquals(testProduct, testRecentlyViewedProducts.getRecentlyViewedProductsList().get(0));
    }


    @Test
    void add_productNotNull() {
        assertDoesNotThrow(() -> service.add(testRecentlyViewedProducts, testProduct));
    }

    @Test
    void add_productCorrect() {
        service.add(testRecentlyViewedProducts, testProduct);
        assertEquals(testProduct.getId(), testRecentlyViewedProducts.getRecentlyViewedProductsList().get(0).getId());
    }
}

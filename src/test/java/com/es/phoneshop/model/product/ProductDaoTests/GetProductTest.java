package com.es.phoneshop.model.product.ProductDaoTests;

import com.es.phoneshop.model.product.ProductDaoTests.configuration.DemoDataInitializerHashMap;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class GetProductTest {

    @BeforeEach
    void setUp() {}

    @ParameterizedTest
    @MethodSource("com.es.phoneshop.model.product.ProductDaoTests.configuration.ProductDaoArgumentsProvider#productDaoProvider")
    public void testGetProductThatExisting(ProductDao productDao) throws ProductNotFoundException {
        Product result = productDao.getProduct(DemoDataInitializerHashMap.existsProduct.getId());
        assertNotNull(result);
        assertEquals(DemoDataInitializerHashMap.existsProduct, result);
    }

    @ParameterizedTest
    @MethodSource("com.es.phoneshop.model.product.ProductDaoTests.configuration.ProductDaoArgumentsProvider#productDaoProvider")
    public void testGetNonExistingProduct(ProductDao productDao) throws ProductNotFoundException {
        assertThrows(ProductNotFoundException.class, () -> {
            productDao.getProduct(-1L);
        });
    }

    @ParameterizedTest
    @MethodSource("com.es.phoneshop.model.product.ProductDaoTests.configuration.ProductDaoArgumentsProvider#productDaoProvider")
    public void testGetProductWithNullId(ProductDao productDao) throws ProductNotFoundException {
        assertThrows(ProductNotFoundException.class, () -> {
            productDao.getProduct(null);
        });
    }
}

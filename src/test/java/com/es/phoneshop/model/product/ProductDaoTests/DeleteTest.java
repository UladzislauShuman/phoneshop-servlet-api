package com.es.phoneshop.model.product.ProductDaoTests;

import com.es.phoneshop.model.product.ProductDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeleteTest {

    @BeforeEach
    void setUp() {}

    @ParameterizedTest
    @MethodSource("com.es.phoneshop.model.product.ProductDaoTests.configuration.ProductDaoArgumentsProvider#productDaoProvider")
    void testDeleteExisting(ProductDao productDao) {
        int amount = productDao.findProducts(null, null, null).size();
        productDao.delete(1L);
        assertEquals(amount - 1, productDao.findProducts(null, null, null).size());
    }

    @ParameterizedTest
    @MethodSource("com.es.phoneshop.model.product.ProductDaoTests.configuration.ProductDaoArgumentsProvider#productDaoProvider")
    void testDeleteNonExisting(ProductDao productDao) {
        int amount = productDao.findProducts(null, null, null).size();
        productDao.delete(-1L);
        assertEquals(amount, productDao.findProducts(null, null, null).size());
    }

    @ParameterizedTest
    @MethodSource("com.es.phoneshop.model.product.ProductDaoTests.configuration.ProductDaoArgumentsProvider#productDaoProvider")
    void testDeleteNull(ProductDao productDao) {
        int amount = productDao.findProducts(null, null, null).size();
        productDao.delete(null);
        assertEquals(amount, productDao.findProducts(null, null, null).size());
    }
}


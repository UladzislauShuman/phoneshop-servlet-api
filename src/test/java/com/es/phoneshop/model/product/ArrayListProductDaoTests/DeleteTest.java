package com.es.phoneshop.model.product.ArrayListProductDaoTests;

import com.es.phoneshop.model.product.ProductDao;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DeleteTest {
    private static ProductDao productDao;

    @BeforeAll
    public static void setup() {
        productDao = DemoDataInitializer.productDao;
        DemoDataInitializer.setup();
    }

    @Test
    public void testDeleteExisting() {
        int amount = productDao.findProducts(null, null, null).size();
        productDao.delete(1L);
        assertEquals(amount - 1, productDao.findProducts(null, null, null).size());
    }

    @Test
    public void testDeleteNonExisting() {
        int amount = productDao.findProducts(null, null, null).size();
        productDao.delete(-1L); //
        assertEquals(amount, productDao.findProducts(null, null, null).size());
    }

    @Test
    public void testDeleteNull() {
        int amount = productDao.findProducts(null, null, null).size();
        productDao.delete(null);
        assertEquals(amount, productDao.findProducts(null, null, null).size());
    }

    @AfterAll
    public static void afterTests() {
        DemoDataInitializer.afterTest();
    }

}

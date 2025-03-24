package com.es.phoneshop.model.product.ArrayListProductDaoTests;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class GetProductTest {
    private static ProductDao productDao;

    @BeforeAll
    public static void setup(){
        productDao = DemoDataInitializer.productDao;
        DemoDataInitializer.setup();
    }

    @Test
    public void testGetProductThatExisting() throws ProductNotFoundException {
        Product result = productDao.getProduct(DemoDataInitializer.existsProduct.getId());
        assertNotNull(result);
        assertEquals(DemoDataInitializer.existsProduct, result);
    }

    @Test
    public void testGetNonExistingProduct() throws ProductNotFoundException {
        assertThrows(ProductNotFoundException.class, () -> {
            productDao.getProduct(-1L);
        });
    }

    @Test
    public void testGetProductWithNullId() throws ProductNotFoundException {
        assertThrows(ProductNotFoundException.class, () -> {
            productDao.getProduct(null);
        });
    }

    @AfterAll
    public static void afterTest() {
        DemoDataInitializer.afterTest();
    }

}

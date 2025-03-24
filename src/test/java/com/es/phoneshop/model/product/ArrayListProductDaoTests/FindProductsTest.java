package com.es.phoneshop.model.product.ArrayListProductDaoTests;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.cunsomorder.Priority;
import com.es.phoneshop.model.product.cunsomorder.PriorityOrderer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
те, кто с приоритет 1 -- они будут запускаться с одним и тем же Setup
если 2 -- им нужен свой собственный, который в них же и реализуется
и чтобы много раз не перезаписывать, решил попробовать вот так
 */

@TestMethodOrder(PriorityOrderer.class)
public class FindProductsTest {

    private static ProductDao productDao;

    @BeforeAll
    public static void setup(){
        productDao = DemoDataInitializer.productDao;
        DemoDataInitializer.setup();
    }

    @Test
    @Priority(1)
    public void testFindProductsUnEmpty() {
        assertFalse(DemoDataInitializer.productDao.findProducts(null,null,null).isEmpty());
    }

    @Test
    @Priority(2)
    public void testFindProductsCheckStockLessThanZero() {
        initializeProductDaoWithProducts(
                DemoDataInitializer.productWithStockLessZero,
                DemoDataInitializer.productNullPrice
        );
        assertTrue(this.productDao.findProducts(null,null,null).isEmpty());
    }

    private void initializeProductDaoWithProducts(Product... products) {
        productDao.clear();
        for (Product product : products) {
            productDao.save(product);
        }
    }

    @AfterAll
    public static void afterTest() {
        DemoDataInitializer.afterTest();
    }
}

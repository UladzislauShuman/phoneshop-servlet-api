package com.es.phoneshop.model.product.ProductDaoTests;

import com.es.phoneshop.model.product.ProductDaoTests.configuration.DemoDataInitializerHashMap;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.cunsomorder.Priority;
import com.es.phoneshop.model.product.cunsomorder.PriorityOrderer;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
те, кто с приоритет 1 -- они будут запускаться с одним и тем же Setup
если 2 -- им нужен свой собственный, который в них же и реализуется
и чтобы много раз не перезаписывать, решил попробовать вот так
 */

@TestMethodOrder(PriorityOrderer.class)
public class FindProductsTest {

    @BeforeEach
    void setUp() {}

    @ParameterizedTest
    @MethodSource("com.es.phoneshop.model.product.ProductDaoTests.configuration.ProductDaoArgumentsProvider#productDaoProvider")
    @Priority(1)
    public void testFindProductsUnEmpty(ProductDao productDao) {
        assertFalse(DemoDataInitializerHashMap.productDao.findProducts(null,null,null).isEmpty());
    }

    @ParameterizedTest
    @MethodSource("com.es.phoneshop.model.product.ProductDaoTests.configuration.ProductDaoArgumentsProvider#productDaoProvider")
    @Priority(2)
    public void testFindProductsCheckStockLessThanZero(ProductDao productDao) {
        initializeProductDaoWithProducts(
                productDao,
                DemoDataInitializerHashMap.productWithStockLessZero, // вынести в отдельный класс
                DemoDataInitializerHashMap.productNullPrice
        );
        assertTrue(productDao.findProducts(null,null,null).isEmpty());
    }

    private void initializeProductDaoWithProducts(ProductDao productDao,Product... products) {
        productDao.clear();
        for (Product product : products) {
            productDao.save(product);
        }
    }

    @AfterAll
    public static void afterTest() {
        DemoDataInitializerHashMap.afterTest();
    }
}

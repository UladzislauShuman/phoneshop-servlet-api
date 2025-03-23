package com.es.phoneshop.model.product.ProductDaoTests;

import com.es.phoneshop.model.product.ProductDaoTests.configuration.DemoDataInitializerHashMap;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Currency;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConcurrentTest {

    @BeforeEach
    void setUp() {}

    @ParameterizedTest
    @MethodSource("com.es.phoneshop.model.product.ProductDaoTests.configuration.ProductDaoArgumentsProvider#productDaoProvider")
    public void testConcurrentAccess(ProductDao productDao) throws InterruptedException {
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(()  -> {
                try {
                    Currency usd = Currency.getInstance("USD");
                    Product product_ = DemoDataInitializerHashMap.product;
                    productDao.save(product_);

                    List<Product> products = productDao.findProducts(null,null,null);
                    if (!products.isEmpty()) {
                        productDao.delete(products.get(0).getId());
                    }
                    if (!products.isEmpty()) {
                        try {
                            productDao.getProduct(products.get(0).getId());
                        } catch (ProductNotFoundException ignored) {}
                    }
                    productDao.findProducts(null,null,null);
                } catch (ProductNotFoundException e) {
                    return; //
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();
        assertFalse(productDao.findProducts(null,null,null).contains(null));
        assertTrue(productDao.findProducts(null,null,null).size() >= 0);
    }
}

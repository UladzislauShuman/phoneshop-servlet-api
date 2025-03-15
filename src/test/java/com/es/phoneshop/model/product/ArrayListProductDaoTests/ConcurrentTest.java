package com.es.phoneshop.model.product.ArrayListProductDaoTests;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Currency;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConcurrentTest {
    private static ProductDao productDao;

    @BeforeAll
    public static void setup(){
        productDao = DemoDataInitializer.productDao;
        DemoDataInitializer.setup();
    }

    @Test
    public void testConcurrentAccess() throws InterruptedException {
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(()  -> {
                try {
                    Currency usd = Currency.getInstance("USD");
                    Product product_ = DemoDataInitializer.product;
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

    @AfterAll
    public static void afterTest() {
        DemoDataInitializer.afterTest();
    }
}

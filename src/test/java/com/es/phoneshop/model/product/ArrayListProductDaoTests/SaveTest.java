package com.es.phoneshop.model.product.ArrayListProductDaoTests;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.product.cunsomorder.Priority;
import com.es.phoneshop.model.product.cunsomorder.PriorityOrderer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestMethodOrder(PriorityOrderer.class)
public class SaveTest {
    private static ProductDao productDao;

    @BeforeAll
    public static void setup(){
        productDao = DemoDataInitializer.productDao;
        DemoDataInitializer.setup();
    }

    @Test
    @Priority(1)
    public void testSaveNewProduct() throws ProductNotFoundException { //
        productDao.save(DemoDataInitializer.product);
        assertTrue(DemoDataInitializer.product.getId() > 0);
        Product result = productDao.getProduct(Long.valueOf(DemoDataInitializer.product.getId()));
        assertNotNull(result);
        assertEquals("test-product", result.getCode());
    }

    @Test
    @Priority(1)
    public void testSaveExitingProduct() throws ProductNotFoundException {
        Product existsProduct = DemoDataInitializer.existsProduct.clone();
        this.changeProduct(existsProduct);
        productDao.save(existsProduct);
        assertEquals(
                existsProduct,
                productDao.getProduct(existsProduct.getId())
        );
    }
    private void changeProduct(Product product) {
        product.setDescription("changed");
        product.setImageUrl("changed");
        product.setPrice(new BigDecimal(10));
        product.setCurrency(Currency.getInstance("USD"));
        product.setStock(12);
        product.setCode("changed");
    }

    @Test
    @Priority(1)
    public void testSaveNullProduct() throws ProductNotFoundException {
        assertThrows(ProductNotFoundException.class, () -> {
            productDao.save(null);
        });
    }

    @Test
    @Priority(2)
    public void testSaveNewProductWithSomeNullFields() throws ProductNotFoundException {
        productDao.clear();
        Currency usd = Currency.getInstance("USD");
        productDao.save(
                DemoDataInitializer.productWithSomeNullFields
        );
        assertFalse(productDao.findProducts(null,null,null).isEmpty());
    }

    @AfterAll
    public static void afterTest() {
        DemoDataInitializer.afterTest();
    }
}

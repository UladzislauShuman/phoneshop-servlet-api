package com.es.phoneshop.model.product.ProductDaoTests;

import com.es.phoneshop.model.product.ProductDaoTests.configuration.DemoDataInitializerHashMap;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.product.cunsomorder.Priority;
import com.es.phoneshop.model.product.cunsomorder.PriorityOrderer;
import com.es.phoneshop.utils.Constants;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestMethodOrder(PriorityOrderer.class)
public class SaveTest {

    @BeforeEach
    void setUp() {}

    @ParameterizedTest
    @MethodSource("com.es.phoneshop.model.product.ProductDaoTests.configuration.ProductDaoArgumentsProvider#productDaoProvider")
    @Priority(1)
    public void testSaveNewProduct(ProductDao productDao) throws ProductNotFoundException { //
        productDao.save(DemoDataInitializerHashMap.product);
        assertTrue(DemoDataInitializerHashMap.product.getId() > 0);
        Product result = productDao.getProduct(Long.valueOf(DemoDataInitializerHashMap.product.getId()));
        assertNotNull(result);
        assertEquals("test-product", result.getCode());
    }

    @ParameterizedTest
    @MethodSource("com.es.phoneshop.model.product.ProductDaoTests.configuration.ProductDaoArgumentsProvider#productDaoProvider")
    @Priority(1)
    public void testSaveExitingProduct(ProductDao productDao) throws ProductNotFoundException {
        Product existsProduct = DemoDataInitializerHashMap.existsProduct.clone();
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

    @ParameterizedTest
    @MethodSource("com.es.phoneshop.model.product.ProductDaoTests.configuration.ProductDaoArgumentsProvider#productDaoProvider")
    @Priority(1)
    public void testSaveNullProduct(ProductDao productDao) throws ProductNotFoundException {
        assertThrows(ProductNotFoundException.class, () -> {
            productDao.save(null);
        });
    }

    @ParameterizedTest
    @MethodSource("com.es.phoneshop.model.product.ProductDaoTests.configuration.ProductDaoArgumentsProvider#productDaoProvider")
    @Priority(2)
    public void testSaveNewProductWithSomeNullFields(ProductDao productDao) throws ProductNotFoundException {
        productDao.clear();
        Currency usd = Currency.getInstance("USD");
        productDao.save(
                DemoDataInitializerHashMap.productWithSomeNullFields
        );
        assertFalse(productDao.findProducts(Constants.nullString,Constants.nullString,Constants.nullString).isEmpty());
    }
}

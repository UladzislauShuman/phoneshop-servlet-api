package com.es.phoneshop.model.product.ArrayListProductDaoTests;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.SortField;
import com.es.phoneshop.model.product.SortOrder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


public class FindProductsWithSortTest {

    private static ProductDao productDao;

    @BeforeAll
    public static void setup(){
        productDao = DemoDataInitializer.productDao;
        DemoDataInitializer.setup();
    }

    @Test
    public void testFindProductsEmptyQueryDescriptionAsc() {
        List<Product> productsDemo = DemoDataInitializer.getDemoDataDescriptionAsc();
        List<Product> productsDao = productDao.findProducts(
                null,
                SortField.DESCRIPTION.toString(),
                SortOrder.ASC.toString()
        );
        if (isProductsDaoAndDemoEqualSize(productsDao,productsDemo))
            fail("productsDao.size() != productsDemo.size()");
        assertTrue(isProductsDaoAndDemoEqual(productsDao,productsDemo));
    }

    private boolean isProductsDaoAndDemoEqualSize(List<Product> productsDao, List<Product> productsDemo) {
        return productsDao.size() != productsDemo.size();
    }

    private boolean isProductsDaoAndDemoEqual(List<Product> productsDao, List<Product> productsDemo) {
        for (int i = 0; i < productsDao.size(); ++i) {
            if (!productsDao.get(i).equalsWithOutId(productsDemo.get(i))) {
                System.out.println(i);
                System.out.println(productsDao.get(i));
                System.out.println(productsDemo.get(i));
                return false;
            }

        }
        return true;
    }

    @Test
    public void testFindProductsEmptyQueryPriceAsc() {
        List<Product> productsDemo = DemoDataInitializer.getDemoDataPriceAsc();
        List<Product> productsDao = productDao.findProducts(
                null,
                SortField.PRICE.toString(),
                SortOrder.ASC.toString()
        );
        if (isProductsDaoAndDemoEqualSize(productsDao,productsDemo))
            fail("productsDao.size() != productsDemo.size()");
        assertTrue(isProductsDaoAndDemoEqual(productsDao,productsDemo));
    }

    @Test
    public void testFindProductsEmptyQueryDescriptionDesc() {
        List<Product> productsDemo = DemoDataInitializer.getDemoDataDescriptionDesc();
        List<Product> productsDao = productDao.findProducts(
                null,
                SortField.DESCRIPTION.toString(),
                SortOrder.DESC.toString()
        );
        if (isProductsDaoAndDemoEqualSize(productsDao,productsDemo))
            fail("productsDao.size() != productsDemo.size()");

        assertTrue(isProductsDaoAndDemoEqual(productsDao,productsDemo));
    }

    @Test
    public void testFindProductsEmptyQueryPriceDesc() {
        List<Product> productsDemo = DemoDataInitializer.getDemoDataPriceDesc();
        List<Product> productsDao = productDao.findProducts(
                null,
                SortField.PRICE.toString(),
                SortOrder.DESC.toString()
        );

        if (isProductsDaoAndDemoEqualSize(productsDao,productsDemo))
            fail("productsDao.size() != productsDemo.size()");

        assertTrue(isProductsDaoAndDemoEqual(productsDao,productsDemo));
    }

    @Test
    public void testFindProductsQuery() {
        List<Product> productsDemo = DemoDataInitializer.getDemoData_Samsung_Galaxy_S_II();
        List<Product> productsDao = productDao.findProducts(
                "Samsung Galaxy S II",
                null,
                null
        );

        if (isProductsDaoAndDemoEqualSize(productsDao,productsDemo))
            fail("productsDao.size() != productsDemo.size()\n" + productsDao.size() + " != " + productsDemo.size()  );

        assertTrue(isProductsDaoAndDemoEqual(productsDao,productsDemo));
    }

    private void initializeProductDaoForTestFindProductsQueryPriceDesc(List<Product> productsDemo) {
        productDao.clear();
        for (Product product : productsDemo) {
            productDao.save(product);
        }
    }

    @Test
    public void testFindProductsQueryPriceDesc() {
        List<Product> productsDemo = DemoDataInitializer.getDemoData_Samsung_Galaxy_S_II_PriceDesc();
        List<Product> productsDao = productDao.findProducts(
                "Samsung Galaxy S II",
                SortField.PRICE.toString(),
                SortOrder.DESC.toString()
        );

        if (isProductsDaoAndDemoEqualSize(productsDao,productsDemo))
            fail("productsDao.size() != productsDemo.size()\n" + productsDao.size() + " != " + productsDemo.size()  );

        assertTrue(isProductsDaoAndDemoEqual(productsDao,productsDemo));
    }

    @AfterAll
    public static void afterTest() {
        DemoDataInitializer.afterTest();
    }
}

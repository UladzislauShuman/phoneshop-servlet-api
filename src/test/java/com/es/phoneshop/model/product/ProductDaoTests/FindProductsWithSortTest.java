package com.es.phoneshop.model.product.ProductDaoTests;

import com.es.phoneshop.model.product.ProductDaoTests.configuration.DemoDataInitializerHashMap;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.SortField;
import com.es.phoneshop.model.product.SortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


public class FindProductsWithSortTest {

    @BeforeEach
    void setUp() {}


    @ParameterizedTest
    @MethodSource("com.es.phoneshop.model.product.ProductDaoTests.configuration.ProductDaoArgumentsProvider#productDaoProvider")
    public void testFindProductsEmptyQueryDescriptionAsc(ProductDao productDao) {
        List<Product> productsDemo = DemoDataInitializerHashMap.getDemoDataDescriptionAsc();
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

    @ParameterizedTest
    @MethodSource("com.es.phoneshop.model.product.ProductDaoTests.configuration.ProductDaoArgumentsProvider#productDaoProvider")
    public void testFindProductsEmptyQueryPriceAsc(ProductDao productDao) {
        List<Product> productsDemo = DemoDataInitializerHashMap.getDemoDataPriceAsc();
        List<Product> productsDao = productDao.findProducts(
                null,
                SortField.PRICE.toString(),
                SortOrder.ASC.toString()
        );
        if (isProductsDaoAndDemoEqualSize(productsDao,productsDemo))
            fail("productsDao.size() != productsDemo.size()");
        assertTrue(isProductsDaoAndDemoEqual(productsDao,productsDemo));
    }

    @ParameterizedTest
    @MethodSource("com.es.phoneshop.model.product.ProductDaoTests.configuration.ProductDaoArgumentsProvider#productDaoProvider")
    public void testFindProductsEmptyQueryDescriptionDesc(ProductDao productDao) {
        List<Product> productsDemo = DemoDataInitializerHashMap.getDemoDataDescriptionDesc();
        List<Product> productsDao = productDao.findProducts(
                null,
                SortField.DESCRIPTION.toString(),
                SortOrder.DESC.toString()
        );
        if (isProductsDaoAndDemoEqualSize(productsDao,productsDemo))
            fail("productsDao.size() != productsDemo.size()");

        assertTrue(isProductsDaoAndDemoEqual(productsDao,productsDemo));
    }

    @ParameterizedTest
    @MethodSource("com.es.phoneshop.model.product.ProductDaoTests.configuration.ProductDaoArgumentsProvider#productDaoProvider")
    public void testFindProductsEmptyQueryPriceDesc(ProductDao productDao) {
        List<Product> productsDemo = DemoDataInitializerHashMap.getDemoDataPriceDesc();
        List<Product> productsDao = productDao.findProducts(
                null,
                SortField.PRICE.toString(),
                SortOrder.DESC.toString()
        );

        if (isProductsDaoAndDemoEqualSize(productsDao,productsDemo))
            fail("productsDao.size() != productsDemo.size()");

        assertTrue(isProductsDaoAndDemoEqual(productsDao,productsDemo));
    }

    @ParameterizedTest
    @MethodSource("com.es.phoneshop.model.product.ProductDaoTests.configuration.ProductDaoArgumentsProvider#productDaoProvider")
    public void testFindProductsQuery(ProductDao productDao) {
        List<Product> productsDemo = DemoDataInitializerHashMap.getDemoData_Samsung_Galaxy_S_II();
        List<Product> productsDao = productDao.findProducts(
                "Samsung Galaxy S II",
                null,
                null
        );

        if (isProductsDaoAndDemoEqualSize(productsDao,productsDemo))
            fail("productsDao.size() != productsDemo.size()\n" + productsDao.size() + " != " + productsDemo.size()  );

        assertTrue(isProductsDaoAndDemoEqual(productsDao,productsDemo));
    }

    @ParameterizedTest
    @MethodSource("com.es.phoneshop.model.product.ProductDaoTests.configuration.ProductDaoArgumentsProvider#productDaoProvider")
    public void testFindProductsQueryPriceDesc(ProductDao productDao) {
        List<Product> productsDemo = DemoDataInitializerHashMap.getDemoData_Galaxy_S_PriceDesc();
        List<Product> productsDao = productDao.findProducts(
                "Galaxy S",
                SortField.PRICE.toString(),
                SortOrder.DESC.toString()
        );
        if (isProductsDaoAndDemoEqualSize(productsDao,productsDemo))
            fail("productsDao.size() != productsDemo.size()\n" + productsDao.size() + " != " + productsDemo.size()  );

        assertTrue(isProductsDaoAndDemoEqual(productsDao,productsDemo));
    }
}

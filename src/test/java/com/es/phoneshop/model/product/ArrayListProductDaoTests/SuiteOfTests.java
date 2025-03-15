package com.es.phoneshop.model.product.ArrayListProductDaoTests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        DeleteTest.class,
        FindProductsTest.class,
        FindProductsWithSortTest.class,
        SaveTest.class,
        GetProductTest.class,
        ConcurrentTest.class
})
public class SuiteOfTests {}


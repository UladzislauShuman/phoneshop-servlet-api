package com.es.phoneshop.model.product.services;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        DefaultCartServiceTest.class,
        DefaultRecentlyViewedProductsServiceTest.class,
        LinkedListRecentlyViewedProductsTest.class,
        CartTest.class,
        HttpSessionRecentlyViewedProductsStorageTest.class,
        HttpSessionCartStorageTest.class,
        DemoDataInitializerTest.class
})
public class SuiteOfTests {}

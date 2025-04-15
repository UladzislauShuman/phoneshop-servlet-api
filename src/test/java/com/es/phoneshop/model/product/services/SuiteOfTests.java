package com.es.phoneshop.model.product.services;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        DefaultCartServiceTest.class,
        DefaultRecentlyViewedProductsServiceTest.class,
        LinkedListRecentlyViewedProductsTest.class,
        CartTest.class,
        DemoDataInitializerTest.class,
        DefaultDosProtectionServiceTest.class,
        DefaultOrderServiceTest.class
})
public class SuiteOfTests {}

package com.es.phoneshop.web;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        ProductDetailsPageServletTest.class,
        ProductListPageServletTest.class,
        AddCartItemServletTest.class,
        CartPageServletTest.class,
        DeleteCartItemServletTest.class,
        MiniCartServletTest.class
})
public class SuiteOfTests {}

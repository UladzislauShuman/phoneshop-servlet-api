package com.es.phoneshop.web;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        ProductDetailsPageServletTest.class
        //ProductListPageServletTest.class
})
public class SuiteOfTests {}

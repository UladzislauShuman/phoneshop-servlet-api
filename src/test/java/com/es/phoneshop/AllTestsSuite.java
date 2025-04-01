package com.es.phoneshop;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        com.es.phoneshop.web.SuiteOfTests.class,
        com.es.phoneshop.model.product.ProductDaoTests.SuiteOfTests.class,
        com.es.phoneshop.model.product.services.SuiteOfTests.class,
        com.es.phoneshop.model.product.utils.SuiteOfTests.class
})
public class AllTestsSuite {
}

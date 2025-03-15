package com.es.phoneshop;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        com.es.phoneshop.web.SuiteOfTests.class,
        com.es.phoneshop.model.product.ArrayListProductDaoTests.SuiteOfTests.class
})
public class AllTestsSuite {
}

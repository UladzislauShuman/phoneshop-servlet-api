package com.es.phoneshop.model.product.utils;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        HttpSessionCartReaderTest.class,
        HttpSessionRVPReaderTest.class
})
public class SuiteOfTests {}

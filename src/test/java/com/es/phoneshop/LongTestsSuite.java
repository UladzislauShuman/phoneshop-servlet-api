package com.es.phoneshop;

import com.es.phoneshop.model.product.services.DefaultDosProtectionServiceLongTimeTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
      DefaultDosProtectionServiceLongTimeTest.class
})
public class LongTestsSuite {
}

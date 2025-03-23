package com.es.phoneshop.model.product.ProductDaoTests.configuration;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class ProductDaoArgumentsProvider {

    public static Stream<Arguments> productDaoProvider() {
        DemoDataInitializerArrayList.setup();
        DemoDataInitializerHashMap.setup();
        return Stream.of(
                Arguments.of(DemoDataInitializerArrayList.productDao),
                Arguments.of(DemoDataInitializerHashMap.productDao)
        );
    }
}

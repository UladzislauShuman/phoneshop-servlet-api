package com.es.phoneshop.model.product.ProductDaoTests.configuration;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class ProductDaoArgumentsProvider {

    public static Stream<Arguments> productDaoProvider() {
        DemoDataInitializerHashMap.setup();
        return Stream.of(
                Arguments.of(DemoDataInitializerHashMap.productDao)
        );
    }
}

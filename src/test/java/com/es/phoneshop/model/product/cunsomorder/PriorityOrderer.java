package com.es.phoneshop.model.product.cunsomorder;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.MethodOrdererContext;

import java.util.Comparator;

public class PriorityOrderer implements MethodOrderer {

    @Override
    public void orderMethods(MethodOrdererContext context) {
        context.getMethodDescriptors().sort(Comparator.comparingInt(
                method -> {
                    Priority priority = method.findAnnotation(Priority.class).orElse(null);
                    return (priority != null) ? priority.value() : Integer.MAX_VALUE;
                }
        ));
    }
}

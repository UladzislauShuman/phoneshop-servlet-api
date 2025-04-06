package com.es.phoneshop.model.product.services;

import com.es.phoneshop.model.product.cunsomorder.Priority;
import com.es.phoneshop.model.product.cunsomorder.PriorityOrderer;
import com.es.phoneshop.security.DefaultDosProtectionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.TimeUnit;
import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
есть тест
isAllowed_resetCountsAndResetsTheCounter,
что зависит от времени
DefaultDosProtectionService.RESET_PERIOD_SECONDS

 */

@ExtendWith(MockitoExtension.class)
public class DefaultDosProtectionServiceLongTimeTest {

    private final String  ipAddress = "127.0.0.1";

    private DefaultDosProtectionService dosProtectionService;

    @BeforeEach
    void setUp() {
        dosProtectionService = (DefaultDosProtectionService) DefaultDosProtectionService.getInstance();
        try {
            Field countMapField = DefaultDosProtectionService.class.getDeclaredField("countMap");
            countMapField.setAccessible(true);
            Map<?, ?> countMap = (Map<?, ?>) countMapField.get(dosProtectionService);
            countMap.clear();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to clear countMap using reflection", e);
        }
    }

    @Test
    void isAllowed_resetCountsAndResetsTheCounter() throws InterruptedException {
        long threshold = DefaultDosProtectionService.THRESHOLD;

        for (int i = 0; i < threshold + 1; i++) {
            assertTrue(dosProtectionService.isAllowed(ipAddress));
        }
        assertFalse(dosProtectionService.isAllowed(ipAddress));

        TimeUnit.SECONDS.sleep(DefaultDosProtectionService.RESET_PERIOD_SECONDS + 1);

        assertTrue(dosProtectionService.isAllowed(ipAddress));
    }

    @AfterEach
    void tearDown() {
        dosProtectionService.shutdown();
    }
}

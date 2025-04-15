package com.es.phoneshop.model.product.services;

import com.es.phoneshop.security.DefaultDosProtectionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class DefaultDosProtectionServiceTest {

    private final String  ipAddress = "127.0.0.1";
    private final String ip1 = "127.0.0.1";
    private final String ip2 = "127.0.0.2";

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
    void isAllowed_firstRequestFromIp() {
        assertTrue(dosProtectionService.isAllowed(ipAddress));
    }

    @Test
    void isAllowed_multipleRequestsFromIpBelowThreshold() {
        for (int i = 0; i < 50; i++) {
            assertTrue(dosProtectionService.isAllowed(ipAddress));
        }
    }

    @Test
    void isAllowed_requestFromIpReachingThreshold() {
        long threshold = DefaultDosProtectionService.THRESHOLD + 1;

        for (int i = 0; i < threshold; i++) {
            assertTrue(dosProtectionService.isAllowed(ipAddress));
        }

        assertFalse(dosProtectionService.isAllowed(ipAddress));
    }

    @Test
    void isAllowed_requestFromIpExceedingThreshold() {
        long threshold = DefaultDosProtectionService.THRESHOLD;

        for (int i = 0; i <= threshold; i++) {
            dosProtectionService.isAllowed(ipAddress);
        }

        assertFalse(dosProtectionService.isAllowed(ipAddress));
    }

    @Test
    void isAllowed_differentIpsBelowThreshold() {
        for (int i = 0; i < 50; i++) {
            assertTrue(dosProtectionService.isAllowed(ip1));
            assertTrue(dosProtectionService.isAllowed(ip2));
        }
    }

    @AfterEach
    void tearDown() {
        dosProtectionService.shutdown();
    }
}

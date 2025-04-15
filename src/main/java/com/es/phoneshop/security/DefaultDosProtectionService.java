package com.es.phoneshop.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DefaultDosProtectionService implements DosProtectionService{
    public static final long THRESHOLD = 100;
    public static final long RESET_PERIOD_SECONDS = 60;

    private Map<String, Long> countMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static volatile DosProtectionService instance;

    private DefaultDosProtectionService() {
        scheduler.scheduleAtFixedRate(this::resetCounts, RESET_PERIOD_SECONDS, RESET_PERIOD_SECONDS, TimeUnit.SECONDS);
    }

    private void resetCounts() {
        countMap.clear();
    }
    public static DosProtectionService getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (DosProtectionService.class) {
            if (instance == null) {
                instance = new DefaultDosProtectionService();
            }
            return instance;
        }
    }

    @Override
    public boolean isAllowed(String ip) {
        Long count = countMap.get(ip);
        if (count == null) {
            count = 1L;
        } else {
            if (count > THRESHOLD) {
                return false;
            }
            count++;
        }
        countMap.put(ip, count);
        return true;
    }

    @Override
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}

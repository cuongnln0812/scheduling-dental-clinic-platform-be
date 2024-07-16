package com.example.dentalclinicschedulingplatform.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VerificationCodeCache {
    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> expiryTimes = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public VerificationCodeCache() {
        executorService.scheduleAtFixedRate(this::removeExpiredEntries, 0, 1, TimeUnit.MINUTES);
    }

    public void put(String key, String value, long expiryTimeInMinutes) {
        long expiryTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(expiryTimeInMinutes);
        cache.put(key, value);
        expiryTimes.put(key, expiryTime);
    }

    public String get(String key) {
        Long expiryTime = expiryTimes.get(key);
        if (expiryTime == null || System.currentTimeMillis() > expiryTime) {
            cache.remove(key);
            expiryTimes.remove(key);
            return null;
        }
        return cache.get(key);
    }

    private void removeExpiredEntries() {
        long currentTime = System.currentTimeMillis();
        expiryTimes.forEach((key, expiryTime) -> {
            if (currentTime > expiryTime) {
                cache.remove(key);
                expiryTimes.remove(key);
            }
        });
    }
}

package com.taskmanagement.service.impl;

import com.taskmanagement.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Cache Service Implementation
 * Handles Redis caching operations for user sessions, projects, and tasks
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Store value in cache
     */
    @Override
    public void put(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            log.debug("Cached value for key: {}", key);
        } catch (Exception e) {
            log.error("Error caching value for key: {}", key, e);
        }
    }

    /**
     * Store value in cache with expiration
     */
    @Override
    public void put(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            log.debug("Cached value for key: {} with expiration: {} {}", key, timeout, unit);
        } catch (Exception e) {
            log.error("Error caching value with expiration for key: {}", key, e);
        }
    }

    /**
     * Get value from cache
     */
    @Override
    public Object get(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            log.debug("Retrieved cached value for key: {}", key);
            return value;
        } catch (Exception e) {
            log.error("Error retrieving cached value for key: {}", key, e);
            return null;
        }
    }

    /**
     * Get value from cache with type
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null && type.isInstance(value)) {
                log.debug("Retrieved cached value for key: {} of type: {}", key, type.getSimpleName());
                return (T) value;
            }
            return null;
        } catch (Exception e) {
            log.error("Error retrieving cached value for key: {} with type: {}", key, type.getSimpleName(), e);
            return null;
        }
    }

    /**
     * Check if key exists in cache
     */
    @Override
    public boolean exists(String key) {
        try {
            Boolean exists = redisTemplate.hasKey(key);
            return exists != null && exists;
        } catch (Exception e) {
            log.error("Error checking if key exists: {}", key, e);
            return false;
        }
    }

    /**
     * Delete key from cache
     */
    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("Deleted cached value for key: {}", key);
        } catch (Exception e) {
            log.error("Error deleting cached value for key: {}", key, e);
        }
    }

    /**
     * Delete all keys matching pattern
     */
    @Override
    public void deletePattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("Deleted {} cached values matching pattern: {}", keys.size(), pattern);
            }
        } catch (Exception e) {
            log.error("Error deleting cached values for pattern: {}", pattern, e);
        }
    }

    /**
     * Clear all cache
     */
    @Override
    public void clearAll() {
        try {
            Set<String> keys = redisTemplate.keys("*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("Cleared all cached values. Total keys deleted: {}", keys.size());
            }
        } catch (Exception e) {
            log.error("Error clearing all cache", e);
        }
    }

    /**
     * Set expiration for a key
     */
    @Override
    public void setExpiration(String key, long timeout, TimeUnit unit) {
        try {
            redisTemplate.expire(key, timeout, unit);
            log.debug("Set expiration for key: {} to {} {}", key, timeout, unit);
        } catch (Exception e) {
            log.error("Error setting expiration for key: {}", key, e);
        }
    }
}

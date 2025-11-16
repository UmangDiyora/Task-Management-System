package com.taskmanagement.service;

import java.util.concurrent.TimeUnit;

/**
 * Cache Service Interface
 * Handles Redis caching operations
 */
public interface CacheService {

    /**
     * Store value in cache
     * @param key Cache key
     * @param value Value to cache
     */
    void put(String key, Object value);

    /**
     * Store value in cache with expiration
     * @param key Cache key
     * @param value Value to cache
     * @param timeout Timeout value
     * @param unit Time unit
     */
    void put(String key, Object value, long timeout, TimeUnit unit);

    /**
     * Get value from cache
     * @param key Cache key
     * @return Cached value or null if not found
     */
    Object get(String key);

    /**
     * Get value from cache with type
     * @param key Cache key
     * @param type Expected type
     * @return Cached value or null if not found
     */
    <T> T get(String key, Class<T> type);

    /**
     * Check if key exists in cache
     * @param key Cache key
     * @return true if exists, false otherwise
     */
    boolean exists(String key);

    /**
     * Delete key from cache
     * @param key Cache key
     */
    void delete(String key);

    /**
     * Delete all keys matching pattern
     * @param pattern Key pattern (e.g., "user:*")
     */
    void deletePattern(String pattern);

    /**
     * Clear all cache
     */
    void clearAll();

    /**
     * Set expiration for a key
     * @param key Cache key
     * @param timeout Timeout value
     * @param unit Time unit
     */
    void setExpiration(String key, long timeout, TimeUnit unit);
}

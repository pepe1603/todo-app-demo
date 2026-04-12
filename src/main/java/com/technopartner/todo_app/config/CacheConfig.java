package com.technopartner.todo_app.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        try {
            RedisSerializer<Object> serializer = RedisSerializer.json();
            
            RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(5))
                    .serializeKeysWith(RedisSerializationContext.SerializationPair
                            .fromSerializer(new StringRedisSerializer()))
                    .serializeValuesWith(RedisSerializationContext.SerializationPair
                            .fromSerializer(serializer))
                    .disableCachingNullValues();
            
            return RedisCacheManager.builder(connectionFactory)
                    .cacheDefaults(config)
                    .withCacheConfiguration("tasks", 
                            RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(2)))
                    .withCacheConfiguration("taskDetails", 
                            RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(1)))
                    .build();
        } catch (Exception e) {
            return new ConcurrentMapCacheManager("tasks", "taskDetails");
        }
    }
}
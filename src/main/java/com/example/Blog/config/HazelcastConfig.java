package com.example.Blog.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableCaching
public class HazelcastConfig {

    @Bean
    public Config hazelcastConfiguration() {
        return new Config()
                .setInstanceName("hazelcast-instance")
                .addMapConfig(new MapConfig()
                        .setName("*")
                        .setBackupCount(1)
                        .setTimeToLiveSeconds(3600));
    }

    @Bean
    public HazelcastInstance hazelcastInstance(Config hazelcastConfiguration) {
        return Hazelcast.newHazelcastInstance(hazelcastConfiguration);
    }

    @Bean
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheManager(hazelcastInstance);
    }
}

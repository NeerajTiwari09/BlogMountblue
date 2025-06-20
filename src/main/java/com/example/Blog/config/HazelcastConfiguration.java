package com.example.Blog.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@Slf4j
public class HazelcastConfiguration {


    @Bean
    public ClientConfig hazelcastClientConfig() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setClusterName("dev"); // Must match your Dockerized member's cluster name
        clientConfig.getNetworkConfig().addAddress("127.0.0.1:5701");
        // clientConfig.getNetworkConfig().setConnectionAttemptLimit(10);
        // clientConfig.getNetworkConfig().setConnectionAttemptPeriod(3000);
        // clientConfig.getNetworkConfig().setConnectionTimeout(5000);
        return clientConfig;
    }

    @Bean
    public HazelcastInstance hazelcastInstance(ClientConfig clientConfig) {
        return HazelcastClient.newHazelcastClient(clientConfig);
    }

    @Bean
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheManager(hazelcastInstance);
    }

}

//package com.example.Blog.config;
//
//import com.hazelcast.config.Config;
//import com.hazelcast.config.JoinConfig;
//import com.hazelcast.config.MapConfig;
//import com.hazelcast.core.Hazelcast;
//import com.hazelcast.core.HazelcastInstance;
//import com.hazelcast.spring.cache.HazelcastCacheManager;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration(proxyBeanMethods = false)
//@EnableCaching
//public class HazelcastConfiguration {
//
//    @Bean
//    public Config hazelcastConfig() {
//        Config config = new Config()
//                .setClusterName("blog-cluster")
//                .setInstanceName("hazelcast-instance")
//                .addMapConfig(new MapConfig()
//                        .setName("*")
//                        .setBackupCount(1)
//                        .setTimeToLiveSeconds(3600));
//        config.setProperty("hazelcast.discovery.enabled", "false");
//        JoinConfig joinConfig = config.getNetworkConfig().getJoin();
//        joinConfig.getMulticastConfig().setEnabled(false);
//        joinConfig.getTcpIpConfig().setEnabled(false);
//        return config;
//    }
//
//    @Bean
//    public HazelcastInstance hazelcastInstance(Config hazelcastConfiguration) {
//        return Hazelcast.newHazelcastInstance(hazelcastConfiguration);
//    }
//
//    @Bean
//    public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
//        return new HazelcastCacheManager(hazelcastInstance);
//    }
//}

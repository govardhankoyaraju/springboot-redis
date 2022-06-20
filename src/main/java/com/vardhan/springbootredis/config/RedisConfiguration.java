package com.vardhan.springbootredis.config;

import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Slf4j
@EnableCaching
@Configuration
public class RedisConfiguration {

    @Value("${redis.host}")
    String redisHost;

    @Value("${redis.port}")
    int redisPort;

    @Value("${redis.username}")
    String redisUserName;

    @Value("${redis.password}")
    String redisPassword;

    @Value("${redis.cluster}")
    boolean redisCluster;

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        log.info("Started redis connection configuration");
        LettuceConnectionFactory lettuceConnectionFactory;
        if (redisCluster) {
            log.info("Started cluster configuration for redis");
            RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
            clusterConfiguration.setUsername(redisUserName);
            clusterConfiguration.setPassword(RedisPassword.of(redisPassword));
            clusterConfiguration.clusterNode(redisHost, redisPort);

            lettuceConnectionFactory = new MyLettuceConnectionFactory(clusterConfiguration);
        } else {
            log.info("Started standalone configuration for redis");
            RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration();
            standaloneConfiguration.setUsername(redisUserName);
            standaloneConfiguration.setPassword(RedisPassword.none());
            standaloneConfiguration.setHostName(redisHost);
            standaloneConfiguration.setPort(redisPort);

            lettuceConnectionFactory = new LettuceConnectionFactory(standaloneConfiguration);
        }
        return lettuceConnectionFactory;
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate() {
        log.info("Started preparing RedisTemplate.");
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory());
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        log.info("Completed preparing RedisTemplate :" + template);
        return template;
    }

    class MyLettuceConnectionFactory extends LettuceConnectionFactory {
        public MyLettuceConnectionFactory(RedisClusterConfiguration clusterConfiguration) {
            super(clusterConfiguration);
        }

        @Override
        public void afterPropertiesSet() {
            log.info("Inside afterPropertiesSet method");
            super.afterPropertiesSet();

            DirectFieldAccessor accessor = new DirectFieldAccessor(this);
            AbstractRedisClient client = (AbstractRedisClient) accessor.getPropertyValue("client");

            if (client instanceof RedisClusterClient) {
                log.info("Setting afterProperties for RedisClusterClient");
                log.info("Getting ClusterTopologyRefreshOptions for cluster configuration");
                ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                        .enablePeriodicRefresh(Duration.ofMinutes(10))
                        .enableAllAdaptiveRefreshTriggers()
                        .dynamicRefreshSources(false)
                        .build();
                log.info("Obtained ClusterTopologyRefreshOptions={}", topologyRefreshOptions);

                log.info("Getting ClusterClientOptions for cluster configuration");
                ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
                        .topologyRefreshOptions(topologyRefreshOptions)
                        .autoReconnect(true)
                        .disconnectedBehavior(ClientOptions.DisconnectedBehavior.ACCEPT_COMMANDS)
                        .build();
                log.info("Obtained ClusterClientOptions={}", clusterClientOptions);
                ((RedisClusterClient) client).setOptions(clusterClientOptions);
            }
        }
    }
}

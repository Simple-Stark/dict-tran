package com.simplestark.config;

import com.simplestark.cache.JinxDictCache;
import com.simplestark.cache.impl.JinxDictMemoryCache;
import com.simplestark.cache.impl.JinxDictRedisCache;
import com.simplestark.util.JinxDictCoverUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author wangruoheng
 */
@Configuration
public class DictAutoConfig {

    @Value("${spring.data.redis.dict.database:#{null}}")
    private Integer dictDatabase;

    @Bean(name = "dictRedisTemplate")
    @ConditionalOnProperty(prefix = "spring.data.redis.dict", name = "database")
    public RedisTemplate<String, Object> dictRedisTemplate(RedisProperties redisProperties) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        configuration.setDatabase(dictDatabase);
        if (StringUtils.isNotBlank(redisProperties.getPassword())) {
            configuration.setPassword(redisProperties.getPassword());
        }
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(configuration);
        connectionFactory.start();
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return template;
    }

    @Bean
    @ConditionalOnBean(name = "dictRedisTemplate")
    public JinxDictCache jinxDictRedisCache(@Qualifier("dictRedisTemplate") RedisTemplate<String, Object> dictRedisTemplate) {
        return new JinxDictRedisCache(dictRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(name = "dictRedisTemplate")
    public JinxDictCache jinxDictCache() {
        return new JinxDictMemoryCache();
    }

    @Bean
    public JinxDictCoverUtil jinxDictCoverUtil(JinxDictCache jinxDictCache) {
        return new JinxDictCoverUtil(jinxDictCache);
    }
}

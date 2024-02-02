package com.github.alphafoxz.oneboot.starter.cache;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.alphafoxz.oneboot.core.annotations.spring.ConditionalOnPropertyEnum;
import com.github.alphafoxz.oneboot.core.toolkit.coding.CollUtil;
import com.github.alphafoxz.oneboot.core.toolkit.coding.JSONUtil;
import com.github.alphafoxz.oneboot.core.toolkit.coding.SecureUtil;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(CacheStarterProperties.class)
public class CacheStarterAutoConfiguration {
    @Resource
    private CacheStarterProperties cacheStarterProperties;

    @Bean
    @ConditionalOnPropertyEnum(name = "oneboot.starter.cache.cache-strategy", enumClass = CacheStarterProperties.CacheStrategyEnum.class, includeAnyValue = "caffeine")
    public Caffeine<Object, Object> caffeine() {
        return Caffeine.newBuilder()
                .maximumSize(cacheStarterProperties.getCacheMaxSize())
                .expireAfterWrite(Duration.ofMillis(cacheStarterProperties.getCacheExpireMs()));
    }

    @Bean
    @ConditionalOnPropertyEnum(name = "oneboot.starter.cache.cache-strategy", enumClass = CacheStarterProperties.CacheStrategyEnum.class, includeAnyValue = "caffeine")
    public CacheManager caffeineCacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }

    @Bean
    @ConditionalOnPropertyEnum(name = "oneboot.starter.cache.cache-strategy", enumClass = CacheStarterProperties.CacheStrategyEnum.class, includeAnyValue = "redis")
    public RedisCacheConfiguration redisCacheConfiguration() {
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        genericJackson2JsonRedisSerializer.configure(objectMapper -> {
            objectMapper.registerModule(new JavaTimeModule());
        });
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(genericJackson2JsonRedisSerializer));
    }

    @Bean
    @ConditionalOnPropertyEnum(name = "oneboot.starter.cache.cache-strategy", enumClass = CacheStarterProperties.CacheStrategyEnum.class, includeAnyValue = "redis")
    public CacheManager redisCacheManagerBuilderCustomizer(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.lockingRedisCacheWriter(redisConnectionFactory);
        return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration());
    }

    @Bean
    public KeyGenerator serviceCacheKeyGenerator() {
        return (Object target, Method method, Object... params) -> {
            StringBuilder finalResult = new StringBuilder();
            // 必须有类名作为前缀，避免走入 Default 之后取的方法名一样造成无法类型转换
//            finalResult.append(target.getClass().getSimpleName());
//            finalResult.append(":");
//            finalResult.append(method.getName());
//            finalResult.append(":");

            if (params.length == 0) {
                finalResult.append("noParams");
                return finalResult.toString();
            }

            // 只含有一个参数位置，并且是基础类型，则进行特殊处理
            if (params.length == 1) {
                Object param = params[0];
                if (null == param) {
                    finalResult.append("nullParams");
                    return finalResult.toString();
                }
                Class<?> clazz = param.getClass();
                if (checkClassBasicType(clazz)) {
                    finalResult.append(param);
                    return finalResult.toString();
                }
            }

            // 非基础类型或多参数的场景
            StringBuilder paramString = new StringBuilder();
            for (int i = 0; i < params.length; i++) {
                Class<?> clazz = params[i].getClass();
                if (null == params[i]) {
                    paramString.append("nullParams");
                } else if (checkClassBasicType(clazz)) {
                    paramString.append(params[i]);
                } else {
                    paramString.append(JSONUtil.toJsonStr(params[i]));
                }
                if (i != params.length - 1) {
                    paramString.append(":");
                }
            }

            String finalParam = paramString.toString();
            String sha256 = SecureUtil.sha256(finalParam);

            log.debug("redisCacheConfig::serviceCacheKeyGenerator Method <{}>, Param <{}> SHA256 <{}>", method.getName(), finalParam, sha256);

            finalResult.append(sha256);
            return finalResult.toString();
        };
    }

    /**
     * 检查是否为基础类型
     */
    private Boolean checkClassBasicType(Class<?> clazz) {
        // 判断基本类型（boolean、char、byte、short、int、long、float、double）
        if (clazz.isPrimitive()) {
            return true;
        }
        // 判断原始类型
        String classTypeName = clazz.getName();
        List<String> basicTypeList = CollUtil.newArrayList(
                String.class.getName(),
                Boolean.class.getName(),
                Character.class.getName()
        );
        boolean result = basicTypeList.contains(classTypeName);
        if (!result && Number.class.isAssignableFrom(clazz)) {
            result = true;
        }
        return result;
    }
}

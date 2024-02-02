package com.github.alphafoxz.oneboot.starter.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "oneboot.starter.cache")
public class CacheStarterProperties {
    private CacheStrategyEnum cacheStrategy = CacheStrategyEnum.CAFFEINE;
    private Long cacheMaxSize = 10000L;
    private Long cacheExpireMs = 1000L * 60 * 30;

    public static enum CacheStrategyEnum {
        CAFFEINE,
        REDIS;
    }
}

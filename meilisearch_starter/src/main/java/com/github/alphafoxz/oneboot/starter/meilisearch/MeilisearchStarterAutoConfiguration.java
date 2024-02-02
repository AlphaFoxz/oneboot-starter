package com.github.alphafoxz.oneboot.starter.meilisearch;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(MeilisearchStarterProperties.class)
public class MeilisearchStarterAutoConfiguration {
    @Resource
    private MeilisearchStarterProperties meilisearchStarterProperties;

    public MeilisearchStarterAutoConfiguration() {
        log.info("加载Meilisearch自动配置");
    }

    @Bean
    public Client client() {
        return new Client(new Config(meilisearchStarterProperties.getUrl(), meilisearchStarterProperties.getMasterKey()));
    }
}

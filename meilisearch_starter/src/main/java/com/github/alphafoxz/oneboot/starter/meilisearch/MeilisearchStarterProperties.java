package com.github.alphafoxz.oneboot.starter.meilisearch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "oneboot.starter.meilisearch")
public class MeilisearchStarterProperties {
    private String url = "http://127.0.0.1:7700";
    private String masterKey = "123QWEasdzxcfffff";
}

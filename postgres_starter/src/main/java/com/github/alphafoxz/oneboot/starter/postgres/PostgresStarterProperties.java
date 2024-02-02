package com.github.alphafoxz.oneboot.starter.postgres;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "oneboot.starter.postgres")
public class PostgresStarterProperties {
}

package com.github.alphafoxz.oneboot.starter.flowable;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;

@Slf4j
@AutoConfiguration
public class FlowableStarterAutoConfiguration {
    public FlowableStarterAutoConfiguration() {
        log.info("加载Flowable自动配置");
    }
}

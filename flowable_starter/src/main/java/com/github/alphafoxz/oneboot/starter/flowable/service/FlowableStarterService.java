package com.github.alphafoxz.oneboot.starter.flowable.service;

import jakarta.annotation.Resource;
import org.flowable.engine.ProcessEngine;
import org.springframework.stereotype.Service;

@Service
public class FlowableStarterService {
    @Resource
    private ProcessEngine processEngine;
}

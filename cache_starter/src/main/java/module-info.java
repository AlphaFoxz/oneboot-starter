module com.github.alphafoxz.oneboot.starter.cache {
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.github.alphafoxz.oneboot.core;
    requires com.github.benmanes.caffeine;
    requires spring.context;
    requires spring.context.support;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires spring.data.redis;
    requires lombok;
    requires org.slf4j;
    requires jakarta.annotation;
}
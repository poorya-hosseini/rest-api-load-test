package com.poorya.test.config;

import io.netty.handler.logging.LogLevel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.properties")
@Data
public class ApplicationProperties {
    private int responseTimeout;
    private int connectionTimeout;
    private LogLevel httpClientLogLevel;
}

package com.jtzj.datasync.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "elk")
public class ELKConfig {
    private String app;
    private Boolean enableRemoteTrace = false;

    @Data
    public static class RabbitMqProperties {
        private String host;
        private Integer port;
        private String username;
        private String password;
    }
}

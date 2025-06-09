package com.jtzj.datasync;

import com.jtzj.datasync.util.UspLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Slf4j
public class DataSyncApplication extends SpringBootServletInitializer implements EnvironmentAware {

    public static void main(String[] args) {
        SpringApplication.run(DataSyncApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(DataSyncApplication.class);
    }

    @Override
    public void setEnvironment(Environment environment) {
        try {
            log.info("开始初始化 ELKLogger");
            UspLogger.init(environment);
        } catch (Exception e) {
            log.error("ELKLogger init error.", e);
        }
    }
}

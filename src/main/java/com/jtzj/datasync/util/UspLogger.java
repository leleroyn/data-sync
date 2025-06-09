package com.jtzj.datasync.util;

import com.jiangtunzj.utils.log.ElkLogger;
import com.jiangtunzj.utils.log.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.io.PrintWriter;
import java.io.StringWriter;

public class UspLogger {
    private static final Logger logger = LoggerFactory.getLogger(UspLogger.class);
    private static Boolean inited = false;
    private Logger innerLogger;

    private UspLogger() {
    }

    public UspLogger(Logger slfLogger) {
        this.innerLogger = slfLogger;
    }

    public static void init(Environment environment) {
        if (!inited) {
            try {
                ElkLogger.init(environment.getProperty("elk.app")
                        , environment.getProperty("elk.source-host")
                        , ElkLogger.RabbitMQProperty.builder()
                                .host(environment.getProperty("elk.rabbitmq.host"))
                                .port(environment.getProperty("elk.rabbitmq.port", Integer.class))
                                .userName(environment.getProperty("elk.rabbitmq.username"))
                                .password(environment.getProperty("elk.rabbitmq.password"))
                                .build()
                        , environment.getProperty("elk.message-limit-size", Integer.class));
                inited = true;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void debug(String content) {
        debug("", content);
    }

    public void debug(String title, String content) {
        try {
            String traceId = ThreadLocalHolder.getRequestId();
            if (innerLogger.isDebugEnabled()) {
                innerLogger.debug(String.format("[%s]%s", traceId, content));
                ElkLogger.log(LogLevel.DEBUG, title, content, traceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void info(String content) {
        info("", content);
    }

    public void info(String title, String content) {
        try {
            String traceId = ThreadLocalHolder.getRequestId();
            if (innerLogger.isInfoEnabled()) {
                innerLogger.info(String.format("[%s]%s", traceId, content));
                ElkLogger.log(LogLevel.INFO, title, content, traceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void error(Exception ex) {
        error(ex.getMessage(), ex);
    }

    public void error(String title) {
        error("", title);
    }

    public void error(String title, String errorStr) {
        try {
            String traceId = ThreadLocalHolder.getRequestId();
            innerLogger.error(String.format("[%s]%s", traceId, errorStr));
            ElkLogger.log(LogLevel.ERROR, title, errorStr, traceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void error(String title, Exception ex) {
        error(title, getErrorInfoFromException(ex));
    }

    public String getErrorInfoFromException(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            sw.close();
            pw.close();
            return "\r\n" + sw + "\r\n";
        } catch (Exception e2) {
            return "ErrorInfoFromException";
        }
    }
}

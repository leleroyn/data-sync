package com.jtzj.datasync.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UspLoggerFactory {
    public static UspLogger getLogger(Class<?> clazz) {
        Logger slfLogger = LoggerFactory.getLogger(clazz);
        return new UspLogger(slfLogger);
    }
}

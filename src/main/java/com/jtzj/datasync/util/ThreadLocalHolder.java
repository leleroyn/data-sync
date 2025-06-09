package com.jtzj.datasync.util;

import com.jtzj.datasync.config.ELKConfig;
import org.springframework.util.StringUtils;

import java.util.Random;

public class ThreadLocalHolder {

    private static class Holder {
        static ThreadLocal<String> threadLocal = ThreadLocal.withInitial(() -> id(8));
    }

    public static String getRequestId() {
        String traceId = Holder.threadLocal.get();
        if (SpringContextUtil.getApplicationContext() != null) {
            ELKConfig elkConfig = SpringContextUtil.getBean(ELKConfig.class);
            String appName = elkConfig.getApp();
            if (elkConfig.getEnableRemoteTrace() && !traceId.startsWith(appName)) {
                traceId = String.join("|", appName, traceId);
            }
        }
        return traceId;
    }

    public static void init() {
        init(id(8));
    }

    public static void init(String requestId) {
        if (StringUtils.hasText(requestId)) {
            Holder.threadLocal.set(requestId);
        } else {
            Holder.threadLocal.set(id(8));
        }
    }

    public static void remove() {
        Holder.threadLocal.remove();
    }

    private final static char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z'};

    /**
     * 随机ID生成器，由数字、小写字母和大写字母组成
     *
     * @param size
     * @return
     */
    private static String id(int size) {
        Random random = new Random();
        char[] cs = new char[size];
        for (int i = 0; i < cs.length; i++) {
            cs[i] = digits[random.nextInt(digits.length)];
        }
        return new String(cs);
    }
}

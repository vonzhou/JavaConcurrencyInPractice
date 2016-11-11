package com.vonzhou.learn.jcip.shutdown;

import java.util.logging.*;

/**
 * UEHLogger
 * <p/>
 * UncaughtExceptionHandler that logs the exception
 * <p>
 * 处理未捕获的异常
 */
public class UEHLogger implements Thread.UncaughtExceptionHandler {
    public void uncaughtException(Thread t, Throwable e) {
        Logger logger = Logger.getAnonymousLogger();
        logger.log(Level.SEVERE, "Thread terminated with exception: " + t.getName(), e);
    }
}

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

    public static void main(String[] args) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                throw new IllegalArgumentException("fake");
            }
        });

        thread.setUncaughtExceptionHandler(new UEHLogger());
        thread.start();

        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

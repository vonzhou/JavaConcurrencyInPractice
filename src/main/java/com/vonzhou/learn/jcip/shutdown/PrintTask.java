package com.vonzhou.learn.jcip.shutdown;

/**
 * Created by vonzhou on 2017/6/4.
 */
public class PrintTask implements Runnable {
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            System.out.println(String.format("[%s] task running ....", System.currentTimeMillis()));
        }
    }
}

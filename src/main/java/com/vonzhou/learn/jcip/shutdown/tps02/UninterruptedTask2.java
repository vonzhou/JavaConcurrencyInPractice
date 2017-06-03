package com.vonzhou.learn.jcip.shutdown.tps02;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 是可以终止的!和原文表述不符
 * Created by vonzhou on 2017/6/3.
 */
public class UninterruptedTask2 implements Runnable {

    public void run() {
        doTask();
    }

    static volatile int flag = 1;

    private void doTask() {
        while (flag == 1) {
            System.out.println(String.format("[%s] task running ....", System.currentTimeMillis()));
        }
    }

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.submit(new UninterruptedTask2());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        flag = 0;
        pool.shutdownNow();
    }
}

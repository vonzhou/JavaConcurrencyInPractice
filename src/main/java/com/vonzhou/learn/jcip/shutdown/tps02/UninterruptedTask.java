package com.vonzhou.learn.jcip.shutdown.tps02;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 任务不会被终止
 * 如果任务里面有Thread.sleep(),那么类似的可中断阻塞方法可以接收到中断信息
 * Created by vonzhou on 2017/6/3.
 */
public class UninterruptedTask implements Runnable {

    public void run() {
        doTask();
    }

    private void doTask() {
        while (true) {
            System.out.println(String.format("[%s] task running ....", System.currentTimeMillis()));
        }
    }

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.submit(new UninterruptedTask());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pool.shutdownNow();
    }
}

package com.vonzhou.learn.jcip.shutdown.tps02;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * https://www.securecoding.cert.org/confluence/display/java/TPS02-J.+Ensure+that+tasks+submitted+to+a+thread+pool+are+interruptible
 * <p>
 * <p>
 * 能终止!
 * Created by vonzhou on 2017/6/3.
 */
public class InterruptibleTask1 implements Runnable {

    public void run() {
        doTask();
    }

    private void doTask() {
        while (!Thread.interrupted()) {
            System.out.println(String.format("[%s] task running ....", System.currentTimeMillis()));
        }
    }

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.submit(new InterruptibleTask1());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pool.shutdownNow();
    }
}

package com.vonzhou.learn.jcip.pcpp.week1;// For week 1

import java.io.IOException;

public class TestLongCounter2 {
    public static void main(String[] args) throws IOException {
        final LongCounter lc = new LongCounter();
        Thread t = new Thread(new Runnable() {
            public void run() {
                while (true)        // Forever call increment
                    lc.increment();
            }
        });
        t.start();
        System.out.println("Press Enter to get the current value:");
        while (true) {
            System.in.read();         // Wait for enter key
            System.out.println(lc.get());
        }
    }
}

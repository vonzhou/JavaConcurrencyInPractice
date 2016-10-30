package com.vonzhou.learn.jcip.pcpp.week1;// For week 2
// sestoft@itu.dk * 2014-08-25

import java.io.IOException;

public class TestMutableInteger {
    public static void main(String[] args) throws IOException {
        final MutableInteger mi = new MutableInteger();
        Thread t = new Thread(() -> {
            while (mi.get() == 0)        // Loop while zero
            {
            }
            System.out.println("I completed, mi = " + mi.get());
        });
        t.start();
        System.out.println("Press Enter to set mi to 42:");
        System.in.read();                   // Wait for enter key
        mi.set(42);
        System.out.println("mi set to 42, waiting for thread ...");
        try {
            t.join();
        } catch (InterruptedException exn) {
        }
        System.out.println("Thread t completed, and so does main");
    }
}

// From Goetz p. 36.  WARNING: Useless without "volatile" or
// "synchronized" to ensure visibility of writes across threads:
class MutableInteger {
//    private volatile int value = 0;
    private int value = 0;

    public void set(int value) {
        this.value = value;
    }

    public int get() {
        return value;
    }
}

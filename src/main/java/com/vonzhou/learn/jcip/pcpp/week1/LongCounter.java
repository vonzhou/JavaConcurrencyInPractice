package com.vonzhou.learn.jcip.pcpp.week1;

class LongCounter {
    private long count = 0;

    public synchronized void increment() {
        count = count + 1;
    }

    public synchronized long get() {
        return count;
    }
}
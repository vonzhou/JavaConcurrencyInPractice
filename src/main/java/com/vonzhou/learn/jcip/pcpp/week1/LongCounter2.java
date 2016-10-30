package com.vonzhou.learn.jcip.pcpp.week1;

/**
 * 非线程安全
 */
class LongCounter2 {
    private long count = 0;

    public void increment() {
        count = count + 1;
    }

    public long get() {
        return count;
    }
}
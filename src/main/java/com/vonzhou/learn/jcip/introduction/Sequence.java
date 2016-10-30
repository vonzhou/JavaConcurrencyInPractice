package com.vonzhou.learn.jcip.introduction;

import com.vonzhou.learn.jcip.annotations.GuardedBy;
import com.vonzhou.learn.jcip.annotations.ThreadSafe;

/**
 * Sequence
 *
 * @author Brian Goetz and Tim Peierls
 */

@ThreadSafe
public class Sequence {

    /**
     * 表示使用的锁对象是该对象自身
     */
    @GuardedBy("this")
    private int nextValue;

    public synchronized int getNext() {
        return nextValue++;
    }
}

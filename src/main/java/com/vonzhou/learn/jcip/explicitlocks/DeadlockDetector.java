package com.vonzhou.learn.jcip.explicitlocks;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @version 2016/11/13.
 */
public class DeadlockDetector {

    private final DeadlockHandler deadlockHandler;
    private final long period;
    private final TimeUnit unit;
    private final ThreadMXBean mbean = ManagementFactory.getThreadMXBean();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    final Runnable deadlockCheck = new Runnable() {
        @Override
        public void run() {
            long[] deadlockedThreadIds = DeadlockDetector.this.mbean.findDeadlockedThreads();

            if (deadlockedThreadIds != null) {
                ThreadInfo[] threadInfos = DeadlockDetector.this.mbean.getThreadInfo(deadlockedThreadIds);

                DeadlockDetector.this.deadlockHandler.handleDeadlock(threadInfos);
            }
        }
    };

    public DeadlockDetector(final DeadlockHandler deadlockHandler, final long period, final TimeUnit unit) {
        this.deadlockHandler = deadlockHandler;
        this.period = period;
        this.unit = unit;
    }

    public void start() {
        this.scheduler.scheduleAtFixedRate(
                this.deadlockCheck, this.period, this.period, this.unit);
    }
}

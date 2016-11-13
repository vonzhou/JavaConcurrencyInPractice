package com.vonzhou.learn.jcip.explicitlocks;

import java.lang.management.ThreadInfo;
import java.util.Map;

/**
 * @version 2016/11/13.
 */
public class DeadlockConsoleHandler implements DeadlockHandler {

    @Override
    public void handleDeadlock(final ThreadInfo[] deadlockedThreads) {
        if (deadlockedThreads != null) {
            System.err.println("Deadlock detected!");

            Map<Thread, StackTraceElement[]> stackTraceMap = Thread.getAllStackTraces();
            for (ThreadInfo threadInfo : deadlockedThreads) {

                if (threadInfo != null) {

                    for (Thread thread : Thread.getAllStackTraces().keySet()) {

                        if (thread.getId() == threadInfo.getThreadId()) {
                            System.err.println(threadInfo.toString().trim());

                            for (StackTraceElement ste : thread.getStackTrace()) {
                                System.err.println("t" + ste.toString().trim());
                            }
                        }
                    }
                }
            }
        }
    }
}

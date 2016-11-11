package com.vonzhou.learn.jcip.threadpools;

import com.vonzhou.learn.jcip.annotations.ThreadSafe;

import java.util.concurrent.*;

/**
 * BoundedExecutor
 * <p/>
 * Using a Semaphore to throttle task submission
 * 使用信号量来控制任务队列的数量，因为线程池使用的默认是无界队列
 * 也可以使用一个计数，或者限制任务队列的长度
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class BoundedExecutor {
    private final Executor exec;
    private final Semaphore semaphore;

    public BoundedExecutor(Executor exec, int bound) {
        this.exec = exec;
        this.semaphore = new Semaphore(bound);
    }

    public void submitTask(final Runnable command) throws InterruptedException {
        semaphore.acquire();
        try {
            exec.execute(new Runnable() {
                public void run() {
                    try {
                        command.run();
                    } finally {
                        semaphore.release();
                    }
                }
            });
        } catch (RejectedExecutionException e) {
            semaphore.release();
        }
    }
}

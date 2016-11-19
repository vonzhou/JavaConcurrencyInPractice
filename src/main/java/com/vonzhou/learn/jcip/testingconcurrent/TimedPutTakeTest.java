package com.vonzhou.learn.jcip.testingconcurrent;

import java.util.concurrent.*;

/**
 * TimedPutTakeTest
 * <p/>
 * Testing with a barrier-based timer
 *
 * @author Brian Goetz and Tim Peierls
 */
public class TimedPutTakeTest extends PutTakeTest {
    private BarrierTimer timer = new BarrierTimer();

    public TimedPutTakeTest(int cap, int pairs, int trials) {
        super(cap, pairs, trials);
        barrier = new CyclicBarrier(nPairs * 2 + 1, timer);
    }

    public void test() {
        try {
            timer.clear();
            for (int i = 0; i < nPairs; i++) {
                pool.execute(new PutTakeTest.Producer());
                pool.execute(new PutTakeTest.Consumer());
            }
            barrier.await();
            barrier.await();
            /**
             * 所有任务执行完成之后的时间统计
             */
            long nsPerItem = timer.getTime() / (nPairs * (long) nTrials);
            System.out.print("Throughput: " + nsPerItem + " ns/item");
//            System.out.println(putSum.get() == takeSum.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        int tpt = 100000; // trials per thread
        for (int cap = 1; cap <= 1000; cap *= 10) {
            System.out.println("Capacity: " + cap);
            for (int pairs = 1; pairs <= 128; pairs *= 2) {
                TimedPutTakeTest t = new TimedPutTakeTest(cap, pairs, tpt);
                System.out.print("Pairs: " + pairs + "\t");
                t.test();
                System.out.print("\t");
                Thread.sleep(1000);
                t.test();
                System.out.println();
                Thread.sleep(1000);
            }
        }
        PutTakeTest.pool.shutdown();
    }
}
/*
Capacity: 1
Pairs: 1	Throughput: 5884 ns/item	Throughput: 5173 ns/item
Pairs: 2	Throughput: 6826 ns/item	Throughput: 7393 ns/item
Pairs: 4	Throughput: 7262 ns/item	Throughput: 7211 ns/item
Pairs: 8	Throughput: 7300 ns/item	Throughput: 7251 ns/item
Pairs: 16	Throughput: 7281 ns/item	Throughput: 7311 ns/item
Pairs: 32	Throughput: 7224 ns/item	Throughput: 7198 ns/item
Pairs: 64	Throughput: 7160 ns/item	Throughput: 7185 ns/item
Pairs: 128	Throughput: 7119 ns/item	Throughput: 7096 ns/item
Capacity: 10
Pairs: 1	Throughput: 811 ns/item	Throughput: 772 ns/item
Pairs: 2	Throughput: 741 ns/item	Throughput: 705 ns/item
Pairs: 4	Throughput: 699 ns/item	Throughput: 689 ns/item
Pairs: 8	Throughput: 656 ns/item	Throughput: 687 ns/item
Pairs: 16	Throughput: 668 ns/item	Throughput: 652 ns/item
Pairs: 32	Throughput: 676 ns/item	Throughput: 678 ns/item
Pairs: 64	Throughput: 698 ns/item	Throughput: 676 ns/item
Pairs: 128	Throughput: 658 ns/item	Throughput: 657 ns/item
Capacity: 100
Pairs: 1	Throughput: 184 ns/item	Throughput: 194 ns/item
Pairs: 2	Throughput: 353 ns/item	Throughput: 336 ns/item
Pairs: 4	Throughput: 398 ns/item	Throughput: 401 ns/item
Pairs: 8	Throughput: 390 ns/item	Throughput: 418 ns/item
Pairs: 16	Throughput: 417 ns/item	Throughput: 415 ns/item
Pairs: 32	Throughput: 444 ns/item	Throughput: 448 ns/item
Pairs: 64	Throughput: 400 ns/item	Throughput: 398 ns/item
Pairs: 128	Throughput: 405 ns/item	Throughput: 404 ns/item
Capacity: 1000
Pairs: 1	Throughput: 206 ns/item	Throughput: 211 ns/item
Pairs: 2	Throughput: 287 ns/item	Throughput: 326 ns/item
Pairs: 4	Throughput: 296 ns/item	Throughput: 279 ns/item
Pairs: 8	Throughput: 307 ns/item	Throughput: 294 ns/item
Pairs: 16	Throughput: 344 ns/item	Throughput: 300 ns/item
Pairs: 32	Throughput: 338 ns/item	Throughput: 309 ns/item
Pairs: 64	Throughput: 330 ns/item	Throughput: 334 ns/item
Pairs: 128	Throughput: 326 ns/item	Throughput: 317 ns/item
 */

//package com.vonzhou.learn.jcip.pcpp.week5;// Week 5
//
//// Counting primes, using the executor framework and multiple tasks
//// for better performance.  On the mtlab.itu.dk 2x16-core AMD the
//// thread-local counter and the Java 8 LongAdder scales considerably
//// better than the homegrown LongCounter lock-based (monitor pattern)
//// class.
//
//// sestoft@itu.dk * 2014-09-21, 2015-09-25
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Future;
//import java.util.function.IntToDoubleFunction;
//
//
//public class TestCountPrimesTasks {
//    private static final ExecutorService executor
//            = Executors.newWorkStealingPool();
//    //  = Executors.newCachedThreadPool();
//
//    public static void main(String[] args) {
//        SystemInfo();
//        final int range = 100_000;
//        System.out.println(Mark7("countSequential",
//                i -> countSequential(range)));
//        System.out.println(Mark7(String.format("countParTask1 %6d", 32),
//                i -> countParallelN1(range, 32)));
//        System.out.println(Mark7(String.format("countParTask2 %6d", 32),
//                i -> countParallelN2(range, 32)));
//        System.out.println(Mark7(String.format("countParTask3 %6d", 32),
//                i -> countParallelN3(range, 32)));
//        for (int c = 1; c <= 100; c++) {
//            final int taskCount = c;
//            Mark7(String.format("countParTask1 %6d", taskCount),
//                    i -> countParallelN1(range, taskCount));
//
//        }
//        for (int c = 1; c <= 100; c++) {
//            final int taskCount = c;
//            Mark7(String.format("countParTask2 %6d", taskCount),
//                    i -> countParallelN2(range, taskCount));
//        }
//    }
//
//    private static boolean isPrime(int n) {
//        int k = 2;
//        while (k * k <= n && n % k != 0)
//            k++;
//        return n >= 2 && k * k > n;
//    }
//
//    // Sequential solution
//    private static long countSequential(int range) {
//        long count = 0;
//        final int from = 0, to = range;
//        for (int i = from; i < to; i++)
//            if (isPrime(i))
//                count++;
//        return count;
//    }
//
//    // General parallel solution, using multiple (Runnable) tasks
//    private static long countParallelN1(int range, int taskCount) {
//        final int perTask = range / taskCount;
//        final LongCounter lc = new LongCounter();
//        List<Future<?>> futures = new ArrayList<Future<?>>();
//        for (int t = 0; t < taskCount; t++) {
//            final int from = perTask * t,
//                    to = (t + 1 == taskCount) ? range : perTask * (t + 1);
//            futures.add(executor.submit(() -> {
//                for (int i = from; i < to; i++)
//                    if (isPrime(i))
//                        lc.increment();
//            }));
//        }
//        try {
//            for (Future<?> fut : futures)
//                fut.get();
//        } catch (InterruptedException exn) {
//            System.out.println("Interrupted: " + exn);
//        } catch (ExecutionException exn) {
//            throw new RuntimeException(exn.getCause());
//        }
//        return lc.get();
//    }
//
//    // General parallel solution, using multiple Callable<Long> tasks
//    private static long countParallelN2(int range, int taskCount) {
//        final int perTask = range / taskCount;
//        List<Callable<Long>> tasks = new ArrayList<Callable<Long>>();
//        for (int t = 0; t < taskCount; t++) {
//            final int from = perTask * t,
//                    to = (t + 1 == taskCount) ? range : perTask * (t + 1);
//            tasks.add(() -> {
//                long count = 0;  // Task-local counter
//                for (int i = from; i < to; i++)
//                    if (isPrime(i))
//                        count++;
//                return count;
//            });
//        }
//        long result = 0;
//        try {
//            List<Future<Long>> futures = executor.invokeAll(tasks);
//            for (Future<Long> fut : futures)
//                result += fut.get();
//        } catch (InterruptedException exn) {
//            System.out.println("Interrupted: " + exn);
//        } catch (ExecutionException exn) {
//            throw new RuntimeException(exn.getCause());
//        }
//        return result;
//    }
//
//    // General parallel solution, using multiple Callable<Void> tasks so
//    // as to be able to submit all to the executor in one method call.
//    private static long countParallelN3(int range, int taskCount) {
//        final int perTask = range / taskCount;
//        final LongCounter lc = new LongCounter();
//        List<Callable<Void>> tasks = new ArrayList<Callable<Void>>();
//        for (int t = 0; t < taskCount; t++) {
//            final int from = perTask * t,
//                    to = (t + 1 == taskCount) ? range : perTask * (t + 1);
//            tasks.add(() -> {
//                for (int i = from; i < to; i++)
//                    if (isPrime(i))
//                        lc.increment();
//                return null;
//            });
//        }
//        try {
//            executor.invokeAll(tasks);
//        } catch (InterruptedException exn) {
//            System.out.println("Interrupted: " + exn);
//        }
//        return lc.get();
//    }
//
//    // --- Benchmarking infrastructure ---
//
//    // NB: Modified to show microseconds instead of nanoseconds
//
//    public static double Mark6(String msg, IntToDoubleFunction f) {
//        int n = 10, count = 1, totalCount = 0;
//        double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
//        do {
//            count *= 2;
//            st = sst = 0.0;
//            for (int j = 0; j < n; j++) {
//                Timer t = new Timer();
//                for (int i = 0; i < count; i++)
//                    dummy += f.applyAsDouble(i);
//                runningTime = t.check();
//                double time = runningTime * 1e6 / count; // microseconds
//                st += time;
//                sst += time * time;
//                totalCount += count;
//            }
//            double mean = st / n, sdev = Math.sqrt((sst - mean * mean * n) / (n - 1));
//            System.out.printf("%-25s %15.1f us %10.2f %10d%n", msg, mean, sdev, count);
//        } while (runningTime < 0.25 && count < Integer.MAX_VALUE / 2);
//        return dummy / totalCount;
//    }
//
//    public static double Mark7(String msg, IntToDoubleFunction f) {
//        int n = 10, count = 1, totalCount = 0;
//        double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
//        do {
//            count *= 2;
//            st = sst = 0.0;
//            for (int j = 0; j < n; j++) {
//                Timer t = new Timer();
//                for (int i = 0; i < count; i++)
//                    dummy += f.applyAsDouble(i);
//                runningTime = t.check();
//                double time = runningTime * 1e6 / count; // microseconds
//                st += time;
//                sst += time * time;
//                totalCount += count;
//            }
//        } while (runningTime < 0.25 && count < Integer.MAX_VALUE / 2);
//        double mean = st / n, sdev = Math.sqrt((sst - mean * mean * n) / (n - 1));
//        System.out.printf("%-25s %15.1f us %10.2f %10d%n", msg, mean, sdev, count);
//        return dummy / totalCount;
//    }
//
//    public static void SystemInfo() {
//        System.out.printf("# OS:   %s; %s; %s%n",
//                System.getProperty("os.name"),
//                System.getProperty("os.version"),
//                System.getProperty("os.arch"));
//        System.out.printf("# JVM:  %s; %s%n",
//                System.getProperty("java.vendor"),
//                System.getProperty("java.version"));
//        // The processor identifier works only on MS Windows:
//        System.out.printf("# CPU:  %s; %d \"cores\"%n",
//                System.getenv("PROCESSOR_IDENTIFIER"),
//                Runtime.getRuntime().availableProcessors());
//        java.util.Date now = new java.util.Date();
//        System.out.printf("# Date: %s%n",
//                new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(now));
//    }
//}
//
//class LongCounter {
//    private long count = 0;
//
//    public synchronized void increment() {
//        count = count + 1;
//    }
//
//    public synchronized long get() {
//        return count;
//    }
//}

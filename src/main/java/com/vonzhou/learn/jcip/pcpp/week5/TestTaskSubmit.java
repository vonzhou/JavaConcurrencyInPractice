package com.vonzhou.learn.jcip.pcpp.week5;// For week 5
// sestoft@itu.dk * 2014-09-17, 2015-09-24

// For the executor framework

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

// For the getPage example
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;


public class TestTaskSubmit {
    private static final ExecutorService executor
            // = Executors.newFixedThreadPool(2);
            // = Executors.newCachedThreadPool();
            = Executors.newWorkStealingPool();

    public static void main(String[] args) {
        testRunnableTask();
        testCallableTask();
        // executor.shutdownNow();
    }

    private static void testRunnableTask() {
        Future<?> fut = executor.submit(new Runnable() {
            public void run() {
                System.out.println("Task ran!");
            }
        });
        try {
            fut.get();
        } catch (InterruptedException exn) {
            System.out.println(exn);
        } catch (ExecutionException exn) {
            throw new RuntimeException(exn);
        }
    }

    private static void testRunnableLambdaTask() {
        Future<?> fut = executor.submit(() -> {
            System.out.println("Task ran!");
        });
        try {
            fut.get();
        } catch (InterruptedException exn) {
            System.out.println(exn);
        } catch (ExecutionException exn) {
            throw new RuntimeException(exn);
        }
    }

    private static void testCallableTask() {
        Future<String> fut = executor.submit(new Callable<String>() {
            public String call() throws IOException {
                return getPage("http://www.wikipedia.org", 10);
            }
        });
        try {
            String webpage = fut.get();
            System.out.println(webpage);
        } catch (InterruptedException exn) {
            System.out.println(exn);
        } catch (ExecutionException exn) {
            throw new RuntimeException(exn);
        }
    }

    private static void testCallableLambdaTask() {
        Future<String> fut
                = executor.submit(() -> {
            return getPage("http://www.wikipedia.org", 10);
        });
        try {
            String webpage = fut.get();
            System.out.println(webpage);
        } catch (InterruptedException exn) {
            System.out.println(exn);
        } catch (ExecutionException exn) {
            throw new RuntimeException(exn);
        }
    }

    public static String getPage(String url, int maxLines) throws IOException {
        // This will close the streams after use (JLS 8 para 14.20.3):
        try (BufferedReader in
                     = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < maxLines; i++) {
                String inputLine = in.readLine();
                if (inputLine == null)
                    break;
                else
                    sb.append(inputLine).append("\n");
            }
            return sb.toString();
        }
    }
}

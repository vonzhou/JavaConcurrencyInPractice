package com.vonzhou.learn.jcip.pcpp.week2;// For week 2
// sestoft@itu.dk * 2014-09-07

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class TestCallable {
    public static void main(String[] args)
            throws InterruptedException, IOException {
//        demoSynchronousCallable();
        // demoSynchronousFutureTask();
        demoAsynchronousFutureTask();
        // demoAsynchronousBetterExceptions();
    }

    private static void demoSynchronousCallable() {
        Callable<String> getWiki = new Callable<String>() {
            public String call() throws Exception {
                return getContents("http://www.wikipedia.org/", 10);
            }
        };
        try {
            String homepage = getWiki.call();
            System.out.println(homepage);
        } catch (Exception exn) {
            throw new RuntimeException(exn);
        }
    }

    private static void demoSynchronousFutureTask() {
        Callable<String> getWiki = new Callable<String>() {
            public String call() throws Exception {
                return getContents("https://www.zhihu.com/people/vonzhou", 10);
            }
        };
        FutureTask<String> fut = new FutureTask<String>(getWiki);
        /**
         * 同步调用 Main thread
         */
        fut.run();
        try {
            String homepage = fut.get();
            System.out.println(homepage);
        } catch (Exception exn) {
            throw new RuntimeException(exn);
        }
    }

    private static void demoAsynchronousFutureTask() {
        Callable<String> getWiki = new Callable<String>() {
            public String call() throws Exception {
                return getContents("https://www.zhihu.com/people/vonzhou", 10);
            }
        };
        FutureTask<String> fut = new FutureTask<String>(getWiki);
        Thread t = new Thread(fut);
        t.start();
        try {
            String homepage = fut.get();
            System.out.println(homepage);
        } catch (Exception exn) {
            throw new RuntimeException(exn);
        }
    }

    private static void demoAsynchronousBetterExceptions()
            throws InterruptedException, IOException {
        Callable<String> getWiki = new Callable<String>() {
            public String call() throws Exception {
                return getContents("http://www.wikipedia.org/", 10);
            }
        };
        FutureTask<String> fut = new FutureTask<String>(getWiki);
        Thread t = new Thread(fut);
        t.start();
        try {
            String homepage = fut.get();
            System.out.println(homepage);
        } catch (ExecutionException exn) {
            Throwable cause = exn.getCause();
            if (cause instanceof IOException)
                throw (IOException) cause;
            else
                throw launderThrowable(cause);
        }
    }


    /**
     * Coerce a checked Throwable to an unchecked RuntimeException.
     * <p>
     * sestoft@itu.dk 2014-09-07: This method converts a Throwable
     * (which is a checked exception) into a RuntimeException (which is
     * an unchecked exception) or an IllegalStateException (which is a
     * subclass of RuntimeException and hence unchecked).  It is unclear
     * why RuntimeException and Error are treated differently; both are
     * unchecked.  A simpler (but grosser) approach is to simply throw a
     * new RuntimeException(t), thus wrapping the Throwable, but that
     * may lead to a RuntimeException containing a RuntimeException
     * which is a little strange.  The original
     * java.util.concurrent.ExecutionException that wrapped the
     * Throwable is itself checked and therefore needs to be caught and
     * turned into something less obnoxious.
     *
     * @author Brian Goetz and Tim Peierls
     */

    public static RuntimeException launderThrowable(Throwable t) {
        if (t instanceof RuntimeException)
            return (RuntimeException) t;
        else if (t instanceof Error)
            throw (Error) t;
        else
            throw new IllegalStateException("Not unchecked", t);
    }

    // Read at most maxLines via HTTP connection to url
    public static String getContents(String url, int maxLines) throws IOException {
        URLConnection conn = new URL(url).openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxLines; i++) {
            String inputLine = in.readLine();
            if (inputLine == null)
                break;
            else
                sb.append(inputLine).append("\n");
        }
        in.close();
        return sb.toString();
    }
}

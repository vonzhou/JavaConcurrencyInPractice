package com.vonzhou.learn.jcip.other;

/**
 * @version 2016/11/10.
 */
public class Main {
    public static void main(String[] args) {
        boolean res = false;
        res = func(res);
        System.out.println(res);
    }

    private static boolean func(boolean res) {
        res = true;
        return res;
    }
}

package com.vonzhou.learn.jcip.other;

/**
 * @version 2016/11/18.
 */
public class IntegerNull {
    public static void main(String[] args) {
        Integer integer = null;
        if (integer == null)
            integer = -1;
        func(integer);

    }

    public static void func(int a) {
        System.out.println(a);
    }
}

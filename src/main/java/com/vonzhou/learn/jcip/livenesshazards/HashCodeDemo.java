package com.vonzhou.learn.jcip.livenesshazards;

/**
 * @version 2016/11/13.
 */
public class HashCodeDemo {
    public static void main(String[] args) {
        String s1 = "vonzhojh";
        String s2 = "luyna";
        System.out.println(s1.hashCode());
        System.out.println(System.identityHashCode(s1));
        System.out.println("==============");
        /**
         * 默认的情况是相等的
         */
        Object obj = new Object();
        System.out.println(obj.hashCode());
        System.out.println(System.identityHashCode(obj));


    }
}

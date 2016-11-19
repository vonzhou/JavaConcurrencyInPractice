package com.vonzhou.learn.jcip.performance;

import com.vonzhou.learn.jcip.annotations.GuardedBy;
import com.vonzhou.learn.jcip.annotations.ThreadSafe;

import java.util.*;
import java.util.regex.*;


/**
 * AttributeStore
 * <p/>
 * Holding a lock longer than necessary
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class AttributeStore {
    @GuardedBy("this")
    private final Map<String, String> attributes = new HashMap<String, String>();

    public synchronized boolean userLocationMatches(String name, String regexp) {
        String key = "users." + name + ".location";
        String location = attributes.get(key);
        if (location == null)
            return false;
        else
            return Pattern.matches(regexp, location);
    }
}

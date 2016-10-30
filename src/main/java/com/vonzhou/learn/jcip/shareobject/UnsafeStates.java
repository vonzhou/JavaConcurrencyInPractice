package com.vonzhou.learn.jcip.shareobject;

/**
 * UnsafeStates
 * <p/>
 * Allowing internal mutable state to escape
 *
 * @author Brian Goetz and Tim Peierls
 */
class UnsafeStates {
    private String[] states = new String[]{
            "AK", "AL" /*...*/
    };

    public String[] getStates() {
        return states;
    }

    public static void main(String[] args) {
        String[] states = new UnsafeStates().getStates();
        states[0] = "FAKE";

        System.out.println(new UnsafeStates().getStates()[0]);
    }
}

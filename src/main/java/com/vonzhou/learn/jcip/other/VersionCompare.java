package com.vonzhou.learn.jcip.other;

/**
 * @version 2016/11/11.
 */
public class VersionCompare {
    public static int versionCompare(String version1, String version2) {
        String[] subVersions1 = version1.split("\\.");
        String[] subVersions2 = version2.split("\\.");
        int i = 0;
        while (i < subVersions1.length && i < subVersions2.length && subVersions1[i].equals(subVersions2[i])) {
            i++;
        }
        if (i < subVersions1.length && i < subVersions2.length) {
            Integer a, b;
            int diff = 0;
            try {
                a = Integer.valueOf(subVersions1[i]);
                b = Integer.valueOf(subVersions2[i]);
                diff = a.compareTo(b);
            } catch (NumberFormatException e) {
                diff = subVersions1[i].compareTo(subVersions2[i]);
            }
            return Integer.signum(diff);
        }
        return Integer.signum(subVersions1.length - subVersions2.length);
    }

    public static void main(String[] args) {
        System.out.println(versionCompare("1.2.3", "1.2.3.4"));
        System.out.println(versionCompare("1.2.3", "1.2.3.4"));
        System.out.println(versionCompare("1.2.3.3", "1.2.3.3"));
    }
}

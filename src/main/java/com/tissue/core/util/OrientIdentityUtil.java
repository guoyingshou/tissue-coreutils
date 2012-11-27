package com.tissue.core.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class OrientIdentityUtil {
    private static Pattern p1 = Pattern.compile("^#(\\d+):(\\d+)$");
    private static Pattern p2 = Pattern.compile("^(\\d+)-(\\d+)$");

    /**
     * Input format: #12:234
     */
    public static String encode(String str) {
        Matcher m = p1.matcher(str);
        String id = null;
        String pos = null;
        while(m.find()) {
            id = m.group(1);
            pos = m.group(2);
        }
        return id + "-" + pos;
    }

    /**
     * Input format: 12-234
     */
    public static String decode(String str) {
        Matcher m = p2.matcher(str);
        String id = null;
        String pos = null;
        while(m.find()) {
            id = m.group(1);
            pos = m.group(2);
        }
        return "#" + id + ":" + pos;
    }
}

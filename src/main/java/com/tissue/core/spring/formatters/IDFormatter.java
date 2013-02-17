package com.tissue.core.spring.formatters;

import org.springframework.format.Formatter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Locale;
import java.text.ParseException;

public class IDFormatter implements Formatter<String> {

    private static Pattern p1 = Pattern.compile("^#(\\d+):(\\d+)$");
    private static Pattern p2 = Pattern.compile("^(\\d+)-(\\d+)$");

    public IDFormatter() {
        System.out.println(">>>>IDFormatter constructed<<<<");
    }


    public String print(String rid, Locale locale) {
        Matcher m = p1.matcher(rid);
        String id = null;
        String pos = null;
        while(m.find()) {
            id = m.group(1);
            pos = m.group(2);
        }
        return id + "-" + pos;
    }

    public String parse(String src, Locale locale) throws ParseException {
        Matcher m = p2.matcher(src);
        String id = null;
        String pos = null;
        while(m.find()) {
            id = m.group(1);
            pos = m.group(2);
        }
        return "#" + id + ":" + pos;
    }
}

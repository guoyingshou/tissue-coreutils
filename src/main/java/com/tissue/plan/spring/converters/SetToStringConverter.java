package com.tissue.plan.spring.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class SetToStringConverter implements Converter<Set<String>, String> {

    public String convert(Set<String> src) {
        StringBuilder buf = new StringBuilder();
        for(String s : src) {
            buf.append(s);
            buf.append("  ");
        }

        return buf.toString();
    }
}

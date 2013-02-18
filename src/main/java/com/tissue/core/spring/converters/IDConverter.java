package com.tissue.core.spring.converters;

import org.springframework.core.convert.converter.Converter;
//import com.google.common.base.Splitter;
//import com.google.common.collect.Sets;
//import java.util.Set;

public class IDConverter implements Converter<String, String> {
    public String convert(String src) {
        return src.contains(":") ? "#"+src : src;
        //Iterable<String> split = Splitter.onPattern("\\s").omitEmptyStrings().trimResults().split(src);
        //return Sets.newHashSet(split);
    }
}

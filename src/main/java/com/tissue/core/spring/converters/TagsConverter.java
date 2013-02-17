package com.tissue.core.spring.converters;

import org.springframework.core.convert.converter.Converter;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import java.util.Set;

public class TagsConverter implements Converter<String, Set<String>> {
    public Set<String> convert(String src) {
        Iterable<String> split = Splitter.onPattern("\\s").omitEmptyStrings().trimResults().split(src);
        return Sets.newHashSet(split);
    }
}

package com.tissue.plan.spring.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;

public class StringToSetConverter implements Converter<String, Set<String>> {

    public Set<String> convert(String src) {

        String[] tagsArray = src.split("\\s+");
        List<String> tagsList = Arrays.asList(tagsArray);

        return new HashSet<String>(tagsList);
    }
}

package com.tissue.core.spring.formatters;

import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import java.util.Set;
import java.util.HashSet;

public class IDAnnotationFormatterFactory implements AnnotationFormatterFactory<IDFormat> {

    public Set<Class<?>> getFieldTypes() {
        System.out.println("++++++++++++++++++++++++++++++");
        System.out.println(">>>getFieldTyps<<<");

        Set<Class<?>> set = new HashSet();
        set.add(String.class);
        System.out.println("++++++++++++++++++++++++++++++");
        return set;
    }

    public Parser<String> getParser(IDFormat arg0, Class<?> arg1) {
        System.out.println("++++++++++++++++++++++++++++++");
        System.out.println(">>>getParser<<<");
        System.out.println("arg0: " + arg0);
        System.out.println("arg1: " + arg1);
        System.out.println("++++++++++++++++++++++++++++++");
        return new IDFormatter();
    }

    public Printer<String> getPrinter(IDFormat annotation, Class<?> fieldType) {
        System.out.println("++++++++++++++++++++++++++++++");
        System.out.println(">>>getPrinter<<<");
        System.out.println("annotation: " + annotation);
        System.out.println("fieldType: " + fieldType);
        System.out.println("++++++++++++++++++++++++++++++");
        return new IDFormatter();
    }
}

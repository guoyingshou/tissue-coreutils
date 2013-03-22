package com.tissue.social.spring.converters;

import com.tissue.social.Impression;
import com.tissue.social.dao.ImpressionDao;

import org.springframework.core.convert.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdToImpressionConverter implements Converter<String, Impression> {

    @Autowired
    private ImpressionDao impressionDao;

    public Impression convert(String src) {
        return impressionDao.getImpression("#"+src);
    }
}

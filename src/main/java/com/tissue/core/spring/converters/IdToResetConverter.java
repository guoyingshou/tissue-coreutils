package com.tissue.core.spring.converters;

import com.tissue.core.Reset;
import com.tissue.core.dao.ResetDao;

import org.springframework.core.convert.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdToResetConverter implements Converter<String, Reset> {

    @Autowired
    private ResetDao resetDao;

    public Reset convert(String src) {
        return resetDao.getReset(src);
    }
}

package com.tissue.plan.spring.converters;

import com.tissue.plan.Answer;
import com.tissue.plan.dao.AnswerDao;

import org.springframework.core.convert.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdToAnswerConverter implements Converter<String, Answer> {

    @Autowired
    private AnswerDao answerDao;

    public Answer convert(String src) {
        return answerDao.getAnswer("#"+src);
    }
}

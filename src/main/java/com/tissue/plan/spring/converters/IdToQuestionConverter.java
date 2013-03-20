package com.tissue.plan.spring.converters;

import com.tissue.plan.Question;
import com.tissue.plan.dao.QuestionDao;

import org.springframework.core.convert.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdToQuestionConverter implements Converter<String, Question> {

    @Autowired
    private QuestionDao questionDao;

    public Question convert(String src) {
        return questionDao.getQuestion("#"+src);
    }
}

package com.tissue.plan.spring.converters;

import com.tissue.plan.QuestionComment;
import com.tissue.plan.dao.QuestionCommentDao;

import org.springframework.core.convert.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;

//@Component
public class IdToQuestionCommentConverter implements Converter<String, QuestionComment> {

    @Autowired
    private QuestionCommentDao questionCommentDao;

    public QuestionComment convert(String src) {
        return questionCommentDao.getQuestionComment("#"+src);
    }
}

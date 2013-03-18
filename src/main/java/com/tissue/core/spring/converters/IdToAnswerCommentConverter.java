package com.tissue.core.spring.converters;

import com.tissue.core.plan.AnswerComment;
import com.tissue.core.plan.dao.AnswerCommentDao;

import org.springframework.core.convert.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdToAnswerCommentConverter implements Converter<String, AnswerComment> {

    @Autowired
    private AnswerCommentDao answerCommentDao;

    public AnswerComment convert(String src) {
        return answerCommentDao.getAnswerComment("#"+src);
    }
}

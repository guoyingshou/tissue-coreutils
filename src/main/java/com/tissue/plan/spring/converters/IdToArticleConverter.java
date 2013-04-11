package com.tissue.plan.spring.converters;

import com.tissue.plan.Article;
import com.tissue.plan.dao.ArticleDao;

import org.springframework.core.convert.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;

//@Component
public class IdToArticleConverter implements Converter<String, Article> {

    @Autowired
    private ArticleDao articleDao;

    public Article convert(String src) {
        return articleDao.getArticle("#"+src);
    }
}

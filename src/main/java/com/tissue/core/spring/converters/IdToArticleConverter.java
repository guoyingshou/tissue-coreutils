package com.tissue.core.spring.converters;

import com.tissue.core.plan.Article;
import com.tissue.core.plan.dao.ArticleDao;

import org.springframework.core.convert.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdToArticleConverter implements Converter<String, Article> {

    @Autowired
    private ArticleDao articleDao;

    public Article convert(String src) {
        return articleDao.getArticle("#"+src);
    }
}

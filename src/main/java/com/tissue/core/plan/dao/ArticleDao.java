package com.tissue.core.plan.dao;

import com.tissue.core.plan.Article;

public interface ArticleDao extends PostDao {

    //String create(ArticleCommand command);

    Article getArticle(String articleId);
    
    //void update(ArticleCommand command);

    //void delete(String articleId);
}

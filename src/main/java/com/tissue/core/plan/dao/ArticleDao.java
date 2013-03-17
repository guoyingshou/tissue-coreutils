package com.tissue.core.plan.dao;

import com.tissue.core.command.ArticleCommand;
import com.tissue.core.plan.Article;

public interface ArticleDao extends PostDao {

    String create(ArticleCommand command);

    Article getArticle(String articleId);
    
}

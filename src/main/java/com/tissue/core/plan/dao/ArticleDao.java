package com.tissue.core.plan.dao;

import com.tissue.core.command.ArticleCommand;
import com.tissue.core.plan.Article;

public interface ArticleDao {

    String create(ArticleCommand command);

    Article getArticle(String articleId);

    //Topic getTopic(String articleId);
    
}

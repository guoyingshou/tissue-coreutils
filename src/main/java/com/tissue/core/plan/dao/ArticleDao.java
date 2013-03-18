package com.tissue.core.plan.dao;

import com.tissue.core.plan.Article;

public interface ArticleDao extends PostDao {
    Article getArticle(String articleId);
}

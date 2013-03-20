package com.tissue.plan.dao;

import com.tissue.plan.Article;

public interface ArticleDao extends PostDao {
    Article getArticle(String articleId);
}

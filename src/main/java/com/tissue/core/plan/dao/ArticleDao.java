package com.tissue.core.plan.dao;

import com.tissue.core.plan.Article;
import com.tissue.core.plan.Topic;
import java.util.List;

public interface ArticleDao {

    Article getArticle(String id);

    //List<? extends Post> getLatestPosts(int limit);
    
}

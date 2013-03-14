package com.tissue.core.plan.dao;

import com.tissue.core.plan.Post;
import com.tissue.core.plan.Topic;
import java.util.List;

public interface PostDao {

    List<Post> getLatestPosts(int limit);
   
}

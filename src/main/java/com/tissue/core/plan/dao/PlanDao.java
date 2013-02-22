package com.tissue.core.plan.dao;

import com.tissue.core.command.PlanCommand;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Post;
import java.util.List;

public interface PlanDao {

    String create(PlanCommand planCommand);

    Plan getPlan(String planId);

    void addMember(String planId, String userId);

    /**
     * topic
     */
    Topic getTopic(String planId);

    /**
     * post
     */
    long getPostsCount(String planId);

    List<Post> getPagedPosts(String planId, int page, int size);

}

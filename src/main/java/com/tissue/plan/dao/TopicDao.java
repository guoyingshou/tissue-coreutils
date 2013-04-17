package com.tissue.plan.dao;

import com.tissue.core.dao.ContentDao;
import com.tissue.plan.command.TopicCommand;
import com.tissue.plan.Topic;
import java.util.List;
import java.util.Set;

public interface TopicDao extends ContentDao {

    String create(TopicCommand command);

    void update(TopicCommand command);

    Topic getTopic(String topicId);

    Topic getTopicByPlan(String planId);

    Topic getTopicByPost(String postId);

    List<Topic> getTrendingTopics(int num);

    List<Topic> getFeaturedTopics(int num);

    long getTopicsCount();

    List<Topic> getPagedTopics(int page, int size);

    long getTopicsCountByTag(String tag);

    Set<String> getTopicTags();

    List<Topic> getPagedTopicsByTag(String tag, int page, int size);

    //List<Topic> getNewTopics(String excludingUserId, int limit);

}

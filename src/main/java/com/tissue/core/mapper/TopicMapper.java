package com.tissue.core.mapper;

import com.tissue.core.command.TopicCommand;
import com.tissue.core.social.User;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Plan;

import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

public class TopicMapper {

    public static ODocument convertTopic(TopicCommand command) {

        ODocument doc = new ODocument("Topic");
        doc.field("title", command.getTitle());
        doc.field("content", command.getContent());
        Set<String> tags = command.getTags();
        doc.field("tags", tags);
        //doc.field("tags", command.getTags());
        doc.field("createTime", new Date());

        return doc;
    }

    public static Topic buildTopicSelf(ODocument doc) {
        Topic topic = new Topic();
        //topic.setId(OrientIdentityUtil.encode(doc.getIdentity().toString()));
        topic.setId(doc.getIdentity().toString());

        String title = doc.field("title", String.class);
        topic.setTitle(title);

        String content = doc.field("content", String.class);
        topic.setContent(content);

        Set<String> tags = doc.field("tags", Set.class);
        topic.setTags(tags);

        Date createTime = doc.field("createTime", Date.class);
        topic.setCreateTime(createTime);

        return topic;
    }

    public static Topic buildTopic(ODocument doc) {
        Topic topic = buildTopicSelf(doc);

        Set<ODocument> inEdgesDoc = doc.field("in");
        for(ODocument inEdgeDoc : inEdgesDoc) {
            ODocument userDoc = inEdgeDoc.field("out");
            User user = UserMapper.buildUserSelf(userDoc);
            topic.setUser(user);
        }
        return topic;
    }

    public static Topic buildTopicDetails(ODocument doc) {

        Topic topic = buildTopic(doc);

        List<ODocument> plansDoc = doc.field("plans", List.class);
        if(plansDoc != null) {
            for(ODocument planDoc : plansDoc) {
                Plan plan = PlanMapper.buildPlanDetails(planDoc);
                plan.setTopic(topic);
                topic.addPlan(plan);
            }
        }
        return topic;
    }

}

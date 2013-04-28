package com.tissue.plan.mapper;

import com.tissue.core.Account;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.plan.command.TopicCommand;
import com.tissue.plan.Topic;
import com.tissue.plan.Plan;

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

        return doc;
    }

    public static Topic buildTopic(ODocument doc) {

        Topic topic = new Topic();
        topic.setId(doc.getIdentity().toString());

        String title = doc.field("title", String.class);
        topic.setTitle(title);

        String content = doc.field("content", String.class);
        topic.setContent(content);

        Set<String> tags = doc.field("tags", Set.class);
        topic.setTags(tags);

        Boolean deleted = doc.field("deleted", Boolean.class);
        if(deleted != null) {
            topic.setDeleted(deleted); 
        }

        return topic;
    }

    /**
     * Add plans that belongs to the given topic.
     */
    public static void setupPlans(Topic topic, ODocument topicDoc) {
        List<ODocument> plansDoc = topicDoc.field("plans");

        if(plansDoc != null) {
            for(ODocument planDoc :plansDoc) {
                Plan plan = PlanMapper.buildPlan(planDoc);
                PlanMapper.setupCreatorAndTimestamp(plan, planDoc);
                
                topic.addPlan(plan);
            }
        }
    }

}

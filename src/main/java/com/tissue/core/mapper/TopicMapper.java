package com.tissue.core.mapper;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.social.User;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Plan;

import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class TopicMapper {

    public static ODocument convertTopic(Topic topic) {

        ODocument doc = new ODocument("Topic");
        doc.field("title", topic.getTitle());
        doc.field("content", topic.getContent());
        doc.field("tags", topic.getTags());

        return doc;
    }

    public static Topic buildTopic(ODocument doc) {
        Topic topic = buildTopicWithoutChild(doc);

        Set<String> tags = doc.field("tags", Set.class);
        topic.setTags(tags);

        Set<ODocument> plansDoc = doc.field("plans", Set.class);
        if(plansDoc != null) {
            List<Plan> plans = PlanMapper.buildPlans(plansDoc);
            topic.setPlans(plans);
        }
        return topic;
    }

    public static Topic buildTopicWithoutChild(ODocument doc) {
        Topic topic = new Topic();
        topic.setId(OrientIdentityUtil.encode(doc.getIdentity().toString()));
 
        String title = doc.field("title", String.class);
        topic.setTitle(title);

        String content = doc.field("content", String.class);
        topic.setContent(content);

        Set<ODocument> inEdges = doc.field("in");
        for(ODocument inEdge : inEdges) {
            if("EdgeCreate".equals(inEdge.getClassName())) {
                Date createTime = inEdge.field("createTime", Date.class);
                topic.setCreateTime(createTime);

                ODocument userDoc = inEdge.field("out");
                User user = UserMapper.buildUser(userDoc);
                topic.setUser(user);
                break;
            }
        }
        return topic;
    }
}

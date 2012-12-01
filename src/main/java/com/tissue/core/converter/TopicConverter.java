package com.tissue.core.converter;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.domain.profile.User;
import com.tissue.domain.plan.Topic;
import com.tissue.domain.plan.Plan;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class TopicConverter {

    public static ODocument convertTopic(Topic topic) {
        ODocument doc = new ODocument("Topic");
        doc.field("title", topic.getTitle());
        doc.field("content", topic.getContent());
        doc.field("createTime", topic.getCreateTime());
        doc.field("tags", topic.getTags());

        ORecordId userRecord = new ORecordId(OrientIdentityUtil.decode(topic.getUser().getId()));
        doc.field("user", userRecord);

        return doc;
    }

    public static Topic buildTopic(ODocument doc) {
        String title = doc.field("title", String.class);
        String content = doc.field("content", String.class);
            
        Topic topic = new Topic();
        topic.setId(OrientIdentityUtil.encode(doc.getIdentity().toString()));
        topic.setTitle(title);
        topic.setContent(content);

        Set<ODocument> plansDoc = doc.field("plans", Set.class);
        List<Plan> plans = null;
        if(plansDoc != null) {
            plans = PlanConverter.buildPlans(plansDoc);
        }

        if(plans != null) {
            topic.setPlans(plans);
        }

        return topic;
    }

    public static Topic buildTopicWithoutChild(ODocument doc) {
        String title = doc.field("title", String.class);
        String content = doc.field("content", String.class);
        ODocument userDoc = doc.field("user");
        User user = UserConverter.buildUser(userDoc);
            
        Topic topic = new Topic();
        topic.setId(OrientIdentityUtil.encode(doc.getIdentity().toString()));
        topic.setTitle(title);
        topic.setContent(content);
        topic.setUser(user);

        return topic;
    }


}

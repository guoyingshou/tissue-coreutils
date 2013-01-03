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
        Topic topic = new Topic();
        topic.setId(OrientIdentityUtil.encode(doc.getIdentity().toString()));

        String title = doc.field("title", String.class);
        topic.setTitle(title);

        String content = doc.field("content", String.class);
        topic.setContent(content);

        Date createTime = doc.field("createTime", Date.class);
        topic.setCreateTime(createTime);

        Set<String> tags = doc.field("tags", Set.class);
        topic.setTags(tags);

        Set<ODocument> plansDoc = doc.field("plans", Set.class);
        if(plansDoc != null) {
            List<Plan> plans = PlanConverter.buildPlans(plansDoc);
            topic.setPlans(plans);
        }

        ODocument userDoc = doc.field("user");
        User user = UserConverter.buildUser(userDoc);
        topic.setUser(user);

        return topic;
    }

    public static Topic buildTopicWithoutChild(ODocument doc) {
        Topic topic = new Topic();
        topic.setId(OrientIdentityUtil.encode(doc.getIdentity().toString()));
 
        String title = doc.field("title", String.class);
        topic.setTitle(title);

        String content = doc.field("content", String.class);
        topic.setContent(content);

        Date createTime = doc.field("createTime", Date.class);
        topic.setCreateTime(createTime);

        ODocument userDoc = doc.field("user");
        User user = UserConverter.buildUser(userDoc);
        topic.setUser(user);

        return topic;
    }

}

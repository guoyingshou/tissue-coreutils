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

    public static Topic buildTopic(ODocument doc) {
        if(doc == null) {
            return null;
        }

        String title = doc.field("title", String.class);
        String content = doc.field("content", String.class);
            
        Topic topic = new Topic();
        topic.setId(OrientIdentityUtil.encode(doc.getIdentity().toString()));
        topic.setTitle(title);
        topic.setContent(content);

        Set<ODocument> plansDoc = doc.field("plans", Set.class);
        List<Plan> plans = PlanConverter.buildPlans(plansDoc);

        if(plans != null) {
            topic.setPlans(plans);
        }

        return topic;
    }

    public static Topic buildMiniumTopic(ODocument doc) {
        if(doc == null) {
            return null;
        }

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

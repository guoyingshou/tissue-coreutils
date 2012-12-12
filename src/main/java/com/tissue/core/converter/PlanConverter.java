package com.tissue.core.converter;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.domain.profile.User;
import com.tissue.domain.plan.Plan;
import com.tissue.domain.plan.Topic;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class PlanConverter {

    public static ODocument convertPlan(Plan plan) {

        ODocument doc = new ODocument("Plan");
        doc.field("duration", plan.getDuration());
        doc.field("createTime", plan.getCreateTime());
        doc.field("members", plan.getMembers());

        ORecordId topicRecord = new ORecordId(OrientIdentityUtil.decode(plan.getTopic().getId()));
        doc.field("topic", topicRecord);

        ORecordId userRecord = new ORecordId(OrientIdentityUtil.decode(plan.getUser().getId()));
        doc.field("user", userRecord);
 
        return doc;
    }

    public static List<Plan> buildPlans(Set<ODocument> plansDoc) {
        List<Plan> plans = new ArrayList();

        for(ODocument planDoc : plansDoc) {
            Plan plan = buildPlan(planDoc);
            plans.add(plan);
        }
        return plans;
    }

    public static Plan buildPlan(ODocument planDoc) {
        Integer duration = planDoc.field("duration", Integer.class);
        Date createTime = planDoc.field("createTime", Date.class);

        ODocument topicDoc = planDoc.field("topic");
        Topic topic = TopicConverter.buildTopicWithoutChild(topicDoc);

        ODocument userDoc = planDoc.field("user");
        User user = UserConverter.buildUser(userDoc);

        List<User> members = null;
        Set<ODocument> membersDoc = planDoc.field("members");

        if(membersDoc != null) {
            members = UserConverter.buildMembers(membersDoc);
        }

        Plan plan = new Plan();
        plan.setId(OrientIdentityUtil.encode(planDoc.getIdentity().toString()));
        plan.setDuration(duration);
        plan.setCreateTime(createTime);
        plan.setTopic(topic);
        plan.setUser(user);
        if(members != null) {
            plan.setMembers(members);
        }

        return plan;
    }

}

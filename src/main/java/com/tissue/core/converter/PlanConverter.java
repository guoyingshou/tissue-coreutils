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

        ORecordId topicRecord = new ORecordId(OrientIdentityUtil.decode(plan.getTopic().getId()));
        doc.field("topic", topicRecord);

        return doc;
    }

    public static List<Plan> buildPlans(Set<ODocument> plansDoc) {
        List<Plan> plans = new ArrayList();

        for(ODocument planDoc : plansDoc) {
            if(planDoc != null) {
                Plan plan = buildPlan(planDoc);
                plans.add(plan);
            }
        }
        return plans;
    }

    public static Plan buildPlan(ODocument planDoc) {

        Plan plan = new Plan();
        plan.setId(OrientIdentityUtil.encode(planDoc.getIdentity().toString()));

        Integer duration = planDoc.field("duration", Integer.class);
        plan.setDuration(duration);

        ODocument topicDoc = planDoc.field("topic");
        Topic topic = TopicConverter.buildTopicWithoutChild(topicDoc);
        plan.setTopic(topic);

        Set<ODocument> inEdges = planDoc.field("in");
        for(ODocument inEdge : inEdges) {
            if(inEdge.field("target").equals("plan")) {
                Date createTime = inEdge.field("createTime", Date.class);
                plan.setCreateTime(createTime);

                ODocument userDoc = inEdge.field("out");
                User user = UserConverter.buildUser(userDoc);
                plan.setUser(user);
                break;
            }
        }

        List<User> members = new ArrayList();
        Set<ODocument> inDocs = planDoc.field("in");
        if(inDocs != null) {
            for(ODocument inDoc : inDocs) {
                ODocument memberDoc = inDoc.field("out");
                if(memberDoc != null) {
                    User member = UserConverter.buildUser(memberDoc);
                    members.add(member);
                }
            }
        }
        plan.setMembers(members);

        return plan;
    }

}

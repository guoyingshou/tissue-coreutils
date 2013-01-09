package com.tissue.core.mapper;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.profile.User;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Topic;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class PlanMapper {

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
        Topic topic = TopicMapper.buildTopicWithoutChild(topicDoc);
        plan.setTopic(topic);

        Set<ODocument> inEdges = planDoc.field("in");
        for(ODocument inEdge : inEdges) {
            //if(inEdge.field("target").equals("plan")) {
            if("EdgeHost".equals(inEdge.getClassName())) {
                Date createTime = inEdge.field("createTime", Date.class);
                plan.setCreateTime(createTime);

                ODocument userDoc = inEdge.field("out");
                User user = UserMapper.buildUser(userDoc);
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
                    User member = UserMapper.buildUser(memberDoc);
                    members.add(member);
                }
            }
        }
        plan.setMembers(members);

        return plan;
    }

}

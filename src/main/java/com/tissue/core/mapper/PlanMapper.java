package com.tissue.core.mapper;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.social.User;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Topic;

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
        doc.field("count", 0);
        return doc;
    }

    public static List<Plan> buildPlans(List<ODocument> docs) {
        List<Plan> plans = new ArrayList();
        for(ODocument doc : docs) {
            Plan plan = buildPlan(doc);
            plans.add(plan);
        }
        return plans;
    }

    public static Plan buildPlan(ODocument doc) {

        Plan plan = new Plan();
        plan.setId(OrientIdentityUtil.encode(doc.getIdentity().toString()));

        Integer duration = doc.field("duration", Integer.class);
        plan.setDuration(duration);

        ODocument topicDoc = doc.field("topic");
        Topic topic = TopicMapper.buildTopic(topicDoc);
        plan.setTopic(topic);

        Set<ODocument> inEdges = doc.field("in");
        for(ODocument inEdge : inEdges) {
            String label = inEdge.field("label", String.class);
            if("plan".equals(label)) {
                Date createTime = inEdge.field("createTime", Date.class);
                plan.setCreateTime(createTime);

                ODocument userDoc = inEdge.field("out");
                User user = UserMapper.buildUser(userDoc);
                plan.setUser(user);
                break;
            }
        }
        return plan;
    }

    public static Plan buildPlanDetails(ODocument doc) {
        Plan plan = buildPlan(doc);
        Set<ODocument> inDocs = doc.field("in");
        if(inDocs != null) {
            List<User> members = new ArrayList();
            for(ODocument inDoc : inDocs) {
                ODocument memberDoc = inDoc.field("out");
                if(memberDoc != null) {
                    User member = UserMapper.buildUser(memberDoc);
                    members.add(member);
                }
            }
            plan.setMembers(members);
        }
        return plan;
     }

}

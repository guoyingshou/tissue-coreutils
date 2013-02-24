package com.tissue.core.mapper;

import com.tissue.core.command.PlanCommand;
import com.tissue.core.social.Account;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Topic;

import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class PlanMapper {

    public static ODocument convertPlan(PlanCommand plan) {
        ODocument doc = new ODocument("Plan");
        doc.field("duration", plan.getDuration());
        doc.field("createTime", new Date());
        doc.field("count", 0);
        return doc;
    }

    public static Plan buildPlanSelf(ODocument doc) {

        Plan plan = new Plan();
        plan.setId(doc.getIdentity().toString());

        Integer duration = doc.field("duration", Integer.class);
        plan.setDuration(duration);

        Date createTime = doc.field("createTime", Date.class);
        plan.setCreateTime(createTime);
 
        ODocument topicDoc = doc.field("topic");
        Topic topic = TopicMapper.buildTopic(topicDoc);
        plan.setTopic(topic);

        return plan;
    }

    /**
     */
    public static Plan buildPlan(ODocument doc) {
        Plan plan = buildPlanSelf(doc);

        Set<ODocument> inEdgesDoc = doc.field("in");
        for(ODocument inEdgeDoc : inEdgesDoc) {
            String label = inEdgeDoc.field("label");
            if("hostGroup".equals(label)) {
                ODocument accountDoc = inEdgeDoc.field("out");
                Account account = AccountMapper.buildAccount(accountDoc);
                plan.setAccount(account);
                break;
            }
        }

        return plan;
    }

    public static Plan buildPlanDetails(ODocument doc) {
        Plan plan = buildPlan(doc);
        if(plan.isActive()) { 
            Set<ODocument> inEdgesDoc = doc.field("in");
            if(inEdgesDoc != null) {
                for(ODocument inEdgeDoc : inEdgesDoc) {
                    String label = inEdgeDoc.field("label");
                    if("joinGroup".equals(label)) {
                        ODocument memberDoc = inEdgeDoc.field("out");
                        Account member = AccountMapper.buildAccount(memberDoc);
                        plan.addMember(member);
                    }
                }
            }
        }
        return plan;
     }

}

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

    public static Plan buildPlanSelf(ODocument doc) {

        Plan plan = new Plan();
        plan.setId(OrientIdentityUtil.encode(doc.getIdentity().toString()));

        Integer duration = doc.field("duration", Integer.class);
        plan.setDuration(duration);

        Date createTime = doc.field("createTime", Date.class);
        plan.setCreateTime(createTime);
 
        return plan;
    }

    /**
     */
    public static Plan buildPlan(ODocument doc) {
        Plan plan = buildPlanSelf(doc);

        Set<ODocument> inEdgesDoc = doc.field("in");
        for(ODocument inEdgeDoc : inEdgesDoc) {
            String label = inEdgeDoc.field("label");
            if("plan".equals(label)) {
                ODocument userDoc = inEdgeDoc.field("out");
                User user = UserMapper.buildUserSelf(userDoc);
                plan.setUser(user);
                break;
            }
        }
        return plan;
    }

    public static Plan buildPlanDetails(ODocument doc) {
        Plan plan = buildPlan(doc);
        Set<ODocument> inEdgesDoc = doc.field("in");
        if(inEdgesDoc != null) {
            for(ODocument inEdgeDoc : inEdgesDoc) {
                String label = inEdgeDoc.field("label");
                if("members".equals(label)) {
                    ODocument memberDoc = inEdgeDoc.field("out");
                    User member = UserMapper.buildUserSelf(memberDoc);
                    plan.addMember(member);
                }
            }
        }
        return plan;
     }

}

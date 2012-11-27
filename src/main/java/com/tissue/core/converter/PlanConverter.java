package com.tissue.core.converter;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.domain.profile.User;
import com.tissue.domain.plan.Plan;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class PlanConverter {

    public static List<Plan> buildPlans(Set<ODocument> plansDoc) {
        List<Plan> plans = new ArrayList();

        if(plansDoc != null) {
            for(ODocument planDoc : plansDoc) {
                Plan plan = buildPlan(planDoc);
                plans.add(plan);
            }
        }
        return plans;
    }

    public static Plan buildPlan(ODocument planDoc) {
        Integer duration = planDoc.field("duration", Integer.class);
        Date createTime = planDoc.field("createTime", Date.class);

        ODocument userDoc = planDoc.field("user");
        User user = UserConverter.buildUser(userDoc);

        Set<ODocument> membersDoc = planDoc.field("members");
        List<User> members = UserConverter.buildMembers(membersDoc);

        Plan plan = new Plan();
        plan.setId(OrientIdentityUtil.encode(planDoc.getIdentity().toString()));
        plan.setDuration(duration);
        plan.setCreateTime(createTime);

        plan.setUser(user);
        if(members != null) {
            plan.setMembers(members);
        }

        return plan;
    }

}

package com.tissue.plan.mapper;

import com.tissue.core.Account;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.plan.command.PlanCommand;
import com.tissue.plan.Plan;
import com.tissue.plan.Topic;

import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class PlanMapper {

    public static ODocument convertPlan(PlanCommand plan) {
        ODocument doc = new ODocument("Plan");
        doc.field("duration", plan.getDuration());
        doc.field("count", 0);
        return doc;
    }

    public static Plan buildPlan(ODocument doc) {

        Plan plan = new Plan();
        plan.setId(doc.getIdentity().toString());

        Integer duration = doc.field("duration", Integer.class);
        plan.setDuration(duration);

        Set<ODocument> edgeCreatePlanDocs = doc.field("in");
        for(ODocument edgeCreatePlanDoc : edgeCreatePlanDocs) {
            String label = edgeCreatePlanDoc.field("label");
            if("plan".equals(label)) {
                Date createTime = edgeCreatePlanDoc.field("createTime", Date.class);
                plan.setCreateTime(createTime);

                ODocument accountDoc = edgeCreatePlanDoc.field("out");
                Account account = AccountMapper.buildAccount(accountDoc);
                plan.setAccount(account);
                break;
            }
        }

        return plan;
    }

}

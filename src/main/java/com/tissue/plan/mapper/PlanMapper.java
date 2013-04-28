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
        return doc;
    }

    public static Plan buildPlan(ODocument doc) {

        Plan plan = new Plan();
        plan.setId(doc.getIdentity().toString());

        Integer duration = doc.field("duration", Integer.class);
        plan.setDuration(duration);

        return plan;
    }

    /**
     * Setup createTime and creator of the plan.
     */
    public static void setupCreatorAndTimestamp(Plan plan, ODocument planDoc) {
        Date ctime = planDoc.field("in_[category='plan'].createTime", Date.class);
        plan.setCreateTime(ctime);

        ODocument accountDoc = planDoc.field("in_[category='plan'].out");
        Account account = AccountMapper.buildAccount(accountDoc);
        plan.setAccount(account);
    }
 
}

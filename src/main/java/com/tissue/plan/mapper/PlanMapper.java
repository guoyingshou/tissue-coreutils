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

        return plan;
    }

    public static void postProcessPlan(Plan plan, ODocument doc) {
        Object inDoc = doc.field("in_");

        if(inDoc instanceof ODocument) {
            ODocument inEdgeDoc = (ODocument)inDoc;
            Date ctime = inEdgeDoc.field("createTime", Date.class);
            plan.setCreateTime(ctime);

            ODocument accountDoc = inEdgeDoc.field("out");
            Account account = AccountMapper.buildAccount(accountDoc);
            plan.setAccount(account);
        }
        else {
            Set<ODocument> inEdgeDocs = (Set)inDoc;
            for(ODocument inEdgeDoc : inEdgeDocs) {
                String category = inEdgeDoc.field("category");
                if("plan".equals(category)) {
                    Date ctime = inEdgeDoc.field("createTime", Date.class);
                    plan.setCreateTime(ctime);

                    ODocument accountDoc = inEdgeDoc.field("out");
                    Account account = AccountMapper.buildAccount(accountDoc);
                    plan.setAccount(account);
 
                    break;
                }
            }
        }
    }

}

package com.tissue.pipes;

import com.tissue.social.Activity;
import com.tissue.social.ActivityObject;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.pipes.PipeFunction;
import java.util.List;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlanPipeFunction implements PipeFunction<ODocument, List<Activity>>  {

    private static Logger logger = LoggerFactory.getLogger(PlanPipeFunction.class);

    private List<Activity> activities;

    public PlanPipeFunction(List<Activity> activities) {
        this.activities = activities;
    }

    public List<Activity> compute(ODocument doc) {
        String label = doc.field("label", String.class);
        if("plan".equals(label) || "member".equals(label)) {
            logger.debug("activity type: " + label);

            Activity activity = new Activity();
            ActivityObject who = new ActivityObject();
            ActivityObject what = new ActivityObject();
            activity.setWho(who);
            activity.setWhat(what);

            activity.setLabel(label);
            Date createTime = doc.field("createTime", Date.class);
            activity.setCreateTime(createTime);

            //setup who
            ODocument accountDoc = doc.field("out");
            ODocument userDoc = accountDoc.field("user");
            String displayName = userDoc.field("displayName", String.class);

            who.setId(userDoc.getIdentity().toString());
            who.setDisplayName(displayName);


            //setup what
            ODocument planDoc = doc.field("in");
            ODocument topicDoc = planDoc.field("topic");
            String title = topicDoc.field("title", String.class);

            what.setId(topicDoc.getIdentity().toString());
            what.setDisplayName(title);

            activities.add(activity);
        }
        return null;
    }
}

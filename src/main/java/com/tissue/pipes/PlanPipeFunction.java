package com.tissue.pipes;

import com.tissue.social.Activity;
import com.tissue.social.ActivityObject;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.pipes.PipeFunction;
import java.util.List;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlanPipeFunction extends ActivityPipeFunction  {

    private static Logger logger = LoggerFactory.getLogger(PlanPipeFunction.class);

    public PlanPipeFunction(List<Activity> activities) {
        super(activities);
    }

    public List<Activity> compute(ODocument doc) {
        String category = doc.field("category", String.class);
        if("plan".equals(category) || "member".equals(category)) {

            Activity activity = init(doc);
            activity.setLabel(category);

            /**
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
            */

            //setup what
            //ODocument planDoc = doc.field("in_");

            ODocument whatDoc = doc.field("what");
            ODocument topicDoc = whatDoc.field("topic");
            String title = topicDoc.field("title", String.class);

            activity.getWhat().setId(topicDoc.getIdentity().toString());
            activity.getWhat().setDisplayName(title);

            activities.add(activity);
        }
        return null;
    }
}

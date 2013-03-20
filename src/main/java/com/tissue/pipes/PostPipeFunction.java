package com.tissue.pipes;

import com.tissue.social.Activity;
import com.tissue.social.ActivityObject;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.pipes.PipeFunction;
import java.util.List;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostPipeFunction implements PipeFunction<ODocument, List<Activity>>  {

    private static Logger logger = LoggerFactory.getLogger(PostPipeFunction.class);

    private List<Activity> activities;

    public PostPipeFunction(List<Activity> activities) {
        this.activities = activities;
    }

    public List<Activity> compute(ODocument doc) {
        String label = doc.field("label", String.class);

        if("concept".equals(label) || "note".equals(label) || "tutorial".equals(label) || "question".equals(label)) {
            logger.debug("activity type: " + label);

            Activity activity = new Activity();
            ActivityObject who = new ActivityObject();
            ActivityObject what = new ActivityObject();
            ActivityObject where = new ActivityObject();
            activity.setWho(who);
            activity.setWhat(what);
            activity.setWhere(where);

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
            ODocument postDoc = doc.field("in");
            String title = postDoc.field("title", String.class);

            what.setId(postDoc.getIdentity().toString());
            what.setDisplayName(title);

            //setup where
            ODocument planDoc = postDoc.field("plan");
            ODocument topicDoc = planDoc.field("topic");
            where.setId(topicDoc.getIdentity().toString());

            activities.add(activity);
        }
        return null;
    }
}

package com.tissue.pipes;

import com.tissue.social.Activity;
import com.tissue.social.ActivityObject;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.pipes.PipeFunction;
import java.util.List;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopicPipeFunction implements PipeFunction<ODocument, List<Activity>>  {
    
    private static Logger logger = LoggerFactory.getLogger(TopicPipeFunction.class);

    private List<Activity> activities;

    public TopicPipeFunction(List<Activity> activities) {
        this.activities = activities;
    }

    public List<Activity> compute(ODocument doc) {

        Activity activity = new Activity();
        ActivityObject who = new ActivityObject();
        ActivityObject what = new ActivityObject();
        activity.setWho(who);
        activity.setWhat(what);

        String type = doc.field("type");
        activity.setLabel(type);

        Date createTime = doc.field("createTime", Date.class);
        activity.setCreateTime(createTime);

        ODocument userDoc = doc.field("user");
        who.setId(userDoc.getIdentity().toString());

        String displayName = userDoc.field("displayName", String.class);
        who.setDisplayName(displayName);

        if("plan".equals(type)) {
            ODocument topicDoc = doc.field("topic");
            what.setId(topicDoc.getIdentity().toString());

            String topicTitle = topicDoc.field("title", String.class);
            what.setDisplayName(topicTitle);
        }
        else {
            String rid = doc.field("rid", String.class);
            what.setId(rid);

            String title = doc.field("title", String.class);
            what.setDisplayName(title);
        }

        activities.add(activity);

        /**

        ODocument edgeDoc = doc.field("in_");
        if("topic".equals(type)) {

            Activity activity = new Activity();
            ActivityObject who = new ActivityObject();
            ActivityObject what = new ActivityObject();
            activity.setWho(who);
            activity.setWhat(what);

            activity.setLabel(type);
            Date createTime = doc.field("createTime", Date.class);
            activity.setCreateTime(createTime);

            //setup who
            ODocument accountDoc = edgeDoc.field("out");
            ODocument userDoc = accountDoc.field("user");
            String displayName = userDoc.field("displayName", String.class);

            who.setId(userDoc.getIdentity().toString());
            who.setDisplayName(displayName);

            //setup what
            ODocument topicDoc = edgeDoc.field("in");
            String title = topicDoc.field("title", String.class);

            what.setId(topicDoc.getIdentity().toString());
            what.setDisplayName(title);

            activities.add(activity);
        }
        */
        return null;
    }
}

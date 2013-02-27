package com.tissue.core.pipes;

import com.tissue.core.social.Activity;
import com.tissue.core.social.ActivityObject;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.pipes.PipeFunction;
import java.util.List;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnswerPipeFunction implements PipeFunction<ODocument, List<Activity>>  {
    
    private static Logger logger = LoggerFactory.getLogger(AnswerPipeFunction.class);

    private List<Activity> activities;

    public AnswerPipeFunction(List<Activity> activities) {
        this.activities = activities;
    }

    public List<Activity> compute(ODocument doc) {
        String label = doc.field("label", String.class);

        if("answer".equals(label)) {
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
            ODocument answerDoc = doc.field("in");
            ODocument questionDoc = answerDoc.field("question");
            String title = questionDoc.field("title", String.class);

            what.setId(questionDoc.getIdentity().toString());
            what.setDisplayName(title);

            //setup where
            ODocument planDoc = questionDoc.field("plan");
            ODocument topicDoc = planDoc.field("topic");
            where.setId(topicDoc.getIdentity().toString());

            activities.add(activity);
        }
        return null;
    }
}

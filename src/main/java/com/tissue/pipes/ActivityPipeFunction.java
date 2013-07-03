package com.tissue.pipes;

import com.tissue.social.Activity;
import com.tissue.social.ActivityObject;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.pipes.PipeFunction;
import java.util.List;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ActivityPipeFunction implements PipeFunction<ODocument, List<Activity>>  {
    
    private static Logger logger = LoggerFactory.getLogger(ActivityPipeFunction.class);

    protected List<Activity> activities;

    public ActivityPipeFunction(List<Activity> activities) {
        this.activities = activities;
    }

    protected Activity init(ODocument doc) {
        String category = doc.field("action.category", String.class);
        
        Activity activity = new Activity();

        ActivityObject who = new ActivityObject();
        activity.setWho(who);

        ActivityObject what = new ActivityObject();
        activity.setWhat(what);

        Date createTime = doc.field("action.createTime", Date.class);
        activity.setCreateTime(createTime);

        /**
        ODocument userDoc = null;
        if("member".equals(category)) {
            userDoc = doc.field("action.in.out_Belongs");
        }
        else {
            userDoc = doc.field("action.out.out_Belongs");
        }
        */

        ODocument userDoc = doc.field("action.out.out_Belongs");

        who.setId(userDoc.getIdentity().toString());

        String displayName = userDoc.field("displayName", String.class);
        who.setDisplayName(displayName);

        return activity;
    }

}

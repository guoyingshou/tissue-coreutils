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

            //setup what
            ODocument whatDoc = doc.field("what");
            ODocument topicDoc = whatDoc.field("in_Contains");
            String title = topicDoc.field("title", String.class);

            activity.getWhat().setId(topicDoc.getIdentity().toString());
            activity.getWhat().setDisplayName(title);

            activities.add(activity);
        }
        return null;
    }
}

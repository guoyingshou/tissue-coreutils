package com.tissue.pipes;

import com.tissue.social.Activity;
import com.tissue.social.ActivityObject;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.pipes.PipeFunction;
import java.util.List;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopicPipeFunction extends ActivityPipeFunction  {
    
    private static Logger logger = LoggerFactory.getLogger(TopicPipeFunction.class);

    public TopicPipeFunction(List<Activity> activities) {
        super(activities);
    }

    public List<Activity> compute(ODocument doc) {

        String category = doc.field("category", String.class);
        if("topic".equals(category)) {

            Activity activity = init(doc);
            activity.setLabel(category);

            ODocument whatDoc = doc.field("what");
            activity.getWhat().setId(whatDoc.getIdentity().toString());

            String title = whatDoc.field("title", String.class);
            activity.getWhat().setDisplayName(title);

            activities.add(activity);
        }

        return null;
    }
}

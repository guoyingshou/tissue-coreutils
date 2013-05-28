package com.tissue.pipes;

import com.tissue.social.Activity;
//import com.tissue.social.ActivityObject;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.pipes.PipeFunction;
import java.util.List;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessagePipeFunction extends ActivityPipeFunction  {
    
    private static Logger logger = LoggerFactory.getLogger(MessagePipeFunction.class);

    public MessagePipeFunction(List<Activity> activities) {
        super(activities);
    }

    public List<Activity> compute(ODocument doc) {
        String category = doc.field("category", String.class);

        if("message".equals(category)) {
            logger.debug("activity type: " + category);

            Activity activity = init(doc);
            activity.setLabel(category);

            ODocument articleDoc = doc.field("what.out_MessagesArticle");
            activity.getWhat().setId(articleDoc.getIdentity().toString());

            String title = articleDoc.field("title", String.class);
            activity.getWhat().setDisplayName(title);

            activities.add(activity);
        }
        return null;
    }
}

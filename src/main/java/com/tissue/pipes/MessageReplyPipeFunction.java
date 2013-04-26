package com.tissue.pipes;

import com.tissue.social.Activity;
import com.tissue.social.ActivityObject;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.pipes.PipeFunction;
import java.util.List;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageReplyPipeFunction extends ActivityPipeFunction  {
    
    private static Logger logger = LoggerFactory.getLogger(MessageReplyPipeFunction.class);

    public MessageReplyPipeFunction(List<Activity> activities) {
        super(activities);
    }

    public List<Activity> compute(ODocument doc) {
        String category = doc.field("category", String.class);

        if("messageReply".equals(category)) {
            logger.debug("activity type: " + category);

            Activity activity = init(doc);
            activity.setLabel(category);

            /**
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
            */

            //setup what
            ODocument whatDoc = doc.field("what");
            ODocument messageDoc = whatDoc.field("message");
            ODocument articleDoc = messageDoc.field("article");
            String title = articleDoc.field("title", String.class);

            activity.getWhat().setId(articleDoc.getIdentity().toString());
            activity.getWhat().setDisplayName(title);

            /**
            //setup where(topic)
            ODocument planDoc = articleDoc.field("plan");
            ODocument topicDoc = planDoc.field("topic");

            where.setId(topicDoc.getIdentity().toString());
            */

            activities.add(activity);
        }
        return null;
    }
}

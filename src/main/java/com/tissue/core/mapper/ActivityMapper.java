package com.tissue.core.mapper;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.social.Activity;
import com.tissue.core.social.ActivityObject;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

public class ActivityMapper {

    public static List<Activity> buildActivities(List<ODocument> docs) {
        List<Activity> activities = new ArrayList();
        if(docs != null) {
            for(ODocument doc : docs) {
                if(doc != null) {
                    Activity activity = buildActivity(doc);
                    activities.add(activity);
                }
            }
        }
        return activities;
    }

    /**
     * @params doc out edge from user node
     */
    public static Activity buildActivity(ODocument doc) {

        Activity activity = new Activity();

        Date published = doc.field("createTime", Date.class);
        activity.setPublished(published);

        String label = doc.field("label", String.class);
        activity.setLabel(label);

        ActivityObject who = new ActivityObject();
        activity.setWho(who);

        ActivityObject what = new ActivityObject();
        activity.setWhat(what);

        ActivityObject to = new ActivityObject();
        activity.setTo(to);

        ActivityObject where = new ActivityObject();
        activity.setWhere(where);

        //set up who. out property node is a user
        ODocument userDoc = doc.field("out");
        who.setId(OrientIdentityUtil.encode(userDoc.getIdentity().toString()));
        String displayName = userDoc.field("displayName", String.class);
        who.setDisplayName(displayName);
        who.setObjectType("person");

        //pre set up what. in property's node type depends on the label value
        //the type of whatDoc is determined by the label's value
        ODocument whatDoc = doc.field("in");
        what.setId(OrientIdentityUtil.encode(whatDoc.getIdentity().toString()));

        if("topic".equals(label)) {
            //whatDoc is topicDoc
            String topicTitle = whatDoc.field("title");
            what.setDisplayName(topicTitle);
        }
        if("plan".equals(label) || "members".equals(label)) {
            //whatdoc is plandoc
            what.setDisplayName("plan");

            ODocument whereDoc = whatDoc.field("topic");
            where.setId(OrientIdentityUtil.encode(whereDoc.getIdentity().toString()));

            String topicTitle = whereDoc.field("title");
            where.setDisplayName(topicTitle);
        }
        if("concept".equals(label) || "note".equals(label) || "tutorial".equals(label) || "question".equals(label)) {
            //whatDoc is postDoc('concept', 'note', 'tutorial' or 'question')
            String postTitle = whatDoc.field("title");
            what.setDisplayName(postTitle);

            ODocument planDoc = whatDoc.field("plan");
            ODocument whereDoc = planDoc.field("topic");
            where.setId(OrientIdentityUtil.encode(whereDoc.getIdentity().toString()));
            
            String topicTitle = whereDoc.field("title");
            where.setDisplayName(topicTitle);
        }
        if("postMessage".equals(label)) {
            what.setDisplayName("post message");

            ODocument toDoc = whatDoc.field("post");
            to.setId(OrientIdentityUtil.encode(toDoc.getIdentity().toString()));

            String postTitle = toDoc.field("title", String.class);
            to.setDisplayName(postTitle);

            ODocument planDoc = toDoc.field("plan");
            ODocument whereDoc = planDoc.field("topic");
            where.setId(OrientIdentityUtil.encode(whereDoc.getIdentity().toString()));
            
            String topicTitle = whereDoc.field("title");
            where.setDisplayName(topicTitle);
        }
        if("postMessageComment".equals(label)) {
            what.setDisplayName("post message comment");

            ODocument postMessageDoc = whatDoc.field("postMessage");
            ODocument toDoc = postMessageDoc.field("post");
            to.setId(OrientIdentityUtil.encode(toDoc.getIdentity().toString()));
            String postTitle = toDoc.field("title", String.class);
            to.setDisplayName(postTitle);

            ODocument planDoc = toDoc.field("plan");
            ODocument whereDoc = planDoc.field("topic");
            where.setId(OrientIdentityUtil.encode(whereDoc.getIdentity().toString()));
            
            String topicTitle = whereDoc.field("title");
            where.setDisplayName(topicTitle);
        }
        if("answer".equals(label) || "questionComment".equals(label)) {
            what.setDisplayName("qustion comments or answers");

            ODocument toDoc = whatDoc.field("question");
            to.setId(OrientIdentityUtil.encode(toDoc.getIdentity().toString()));

            String postTitle = toDoc.field("title", String.class);
            to.setDisplayName(postTitle);

            ODocument planDoc = toDoc.field("plan");
            ODocument whereDoc = planDoc.field("topic");
            where.setId(OrientIdentityUtil.encode(whereDoc.getIdentity().toString()));
            
            String topicTitle = whereDoc.field("title");
            where.setDisplayName(topicTitle);
        }
        if("answerComment".equals(label)) {
            what.setDisplayName("answer comment");

            ODocument answerCommentDoc = whatDoc.field("answer");
            ODocument toDoc = answerCommentDoc.field("question");
            to.setId(OrientIdentityUtil.encode(toDoc.getIdentity().toString()));
            String postTitle = toDoc.field("title", String.class);
            to.setDisplayName(postTitle);

            ODocument planDoc = toDoc.field("plan");
            ODocument whereDoc = planDoc.field("topic");
            where.setId(OrientIdentityUtil.encode(whereDoc.getIdentity().toString()));
            
            String topicTitle = whereDoc.field("title");
            where.setDisplayName(topicTitle);
        }
        return activity;
    }

}

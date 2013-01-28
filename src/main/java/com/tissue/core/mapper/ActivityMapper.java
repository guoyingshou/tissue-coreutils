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

    /**
     * @params doc out edge from user node
     */
    public static Activity buildActivity(ODocument doc) {

        Activity activity = new Activity();

        Date createTime = doc.field("createTime", Date.class);
        activity.setCreateTime(createTime);

        String label = doc.field("label", String.class);
        activity.setLabel(label);

        ActivityObject who = new ActivityObject();
        activity.setWho(who);
        ODocument userDoc = doc.field("out");
        who.setId(OrientIdentityUtil.encode(userDoc.getIdentity().toString()));
        String displayName = userDoc.field("displayName", String.class);
        who.setDisplayName(displayName);
        who.setObjectType("person");

        ActivityObject what = new ActivityObject();
        activity.setWhat(what);

        ActivityObject to = new ActivityObject();
        activity.setTo(to);

        ActivityObject where = new ActivityObject();
        activity.setWhere(where);

        ODocument whatDoc = doc.field("in");
        what.setId(OrientIdentityUtil.encode(whatDoc.getIdentity().toString()));

        if("topic".equals(label)) {
            String topicTitle = whatDoc.field("title");
            what.setDisplayName(topicTitle);
        }
        if("plan".equals(label) || "members".equals(label)) {
            what.setDisplayName("plan");

            ODocument whereDoc = whatDoc.field("topic");
            where.setId(OrientIdentityUtil.encode(whereDoc.getIdentity().toString()));

            String topicTitle = whereDoc.field("title");
            where.setDisplayName(topicTitle);
        }
        if("concept".equals(label) || "note".equals(label) || "tutorial".equals(label) || "question".equals(label)) {
            String postTitle = whatDoc.field("title");
            what.setDisplayName(postTitle);

            ODocument planDoc = whatDoc.field("plan");
            ODocument whereDoc = planDoc.field("topic");
            where.setId(OrientIdentityUtil.encode(whereDoc.getIdentity().toString()));
            
            String topicTitle = whereDoc.field("title");
            where.setDisplayName(topicTitle);
        }
        if("postMessage".equals(label)) {
            //what.setDisplayName("post message");
            //
            System.out.println("++++mapper postMessage doc: " + doc);

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

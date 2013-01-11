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

    //what: 'plan'; where: topic
    private static List<String> planNames = Arrays.asList("EdgeHost", "EdgeJoin");

    //what: post; where: topic  
    private static List<String> postNames = Arrays.asList("EdgeConcept", "EdgeNote", "EdgeTutorial", "EdgeQuestion");

    //what: message, questioncomment or answer; to: post; where: topic
    private static List<String> answers = Arrays.asList("EdgeQuestionComment", "EdgeAnswer");


    public static List<Activity> buildStream(List<ODocument> streamDoc) {
        List<Activity> stream = new ArrayList();
        if(streamDoc != null) {
            for(ODocument doc : streamDoc) {
                Activity activity = buildActivity(doc);
                stream.add(activity);
            }
        }
        return stream;
    }

    public static Activity buildActivity(ODocument doc) {

        String className = doc.getClassName();

        Activity activity = new Activity();

        Date published = doc.field("createTime", Date.class);
        activity.setPublished(published);

        String type = doc.field("label", String.class);
        activity.setType(type);

        ActivityObject who = new ActivityObject();
        activity.setWho(who);

        ActivityObject what = new ActivityObject();
        activity.setWhat(what);

        ActivityObject to = new ActivityObject();
        activity.setTo(to);

        ActivityObject where = new ActivityObject();
        activity.setWhere(where);

        //set up who
        ODocument userDoc = doc.field("out");
        who.setId(OrientIdentityUtil.encode(userDoc.getIdentity().toString()));
        String displayName = userDoc.field("displayName", String.class);
        who.setDisplayName(displayName);
        who.setObjectType("person");

        //pre set up what
        ODocument whatDoc = doc.field("in");
        what.setId(OrientIdentityUtil.encode(whatDoc.getIdentity().toString()));

        //the type of whatDoc is determined by the edge class

        if("EdgeCreate".equals(className)) {
            //whatDoc is topicDoc
            String topicTitle = whatDoc.field("title");
            what.setDisplayName(topicTitle);
        }
        if(planNames.contains(className)) {
            //whatdoc is plandoc
            what.setDisplayName("plan");

            ODocument whereDoc = whatDoc.field("topic");
            where.setId(OrientIdentityUtil.encode(whereDoc.getIdentity().toString()));

            String topicTitle = whereDoc.field("title");
            where.setDisplayName(topicTitle);
        }
        if(postNames.contains(className)) {
            //whatDoc is postDoc('concept', 'note', 'tutorial' or 'question')
            String postTitle = whatDoc.field("title");
            what.setDisplayName(postTitle);

            ODocument planDoc = whatDoc.field("plan");
            ODocument whereDoc = planDoc.field("topic");
            where.setId(OrientIdentityUtil.encode(whereDoc.getIdentity().toString()));
            
            String topicTitle = whereDoc.field("title");
            where.setDisplayName(topicTitle);
        }
        if("EdgePostMessage".equals(className)) {
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
        if("EdgePostMessageComment".equals(className)) {
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
        if(answers.contains(className)) {
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
        if("EdgeAnswerComment".equals(className)) {
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

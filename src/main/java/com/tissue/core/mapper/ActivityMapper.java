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
    private static List<String> planNames = Arrays.asList("EdgeHost", "EdgeJoin");
    private static List<String> postNames = Arrays.asList("EdgeConcept", "EdgeNote", "EdgeTutorial", "EdgeQuestion");
    private static List<String> commentNames = Arrays.asList("EdgePostMessage", "EdgePostMessageComment", "EdgeQuestionComment", "EdgeAnswer", "EdgeAnswerComment");

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

        if("EdgeCreate".equals(className)) {
            String topicTitle = whatDoc.field("title");
            what.setDisplayName(topicTitle);
        }
        if("EdgeHost".equals(className)) {
            what.setDisplayName("plan");

            ODocument whereDoc = whatDoc.field("topic");
            where.setId(OrientIdentityUtil.encode(whereDoc.getIdentity().toString()));

            String topicTitle = whereDoc.field("title");
            where.setDisplayName(topicTitle);
        }
        if("EdgeJoin".equals(className)) {
            what.setDisplayName("plan");

            ODocument whereDoc = whatDoc.field("topic");
            where.setId(OrientIdentityUtil.encode(whereDoc.getIdentity().toString()));

            String topicTitle = whereDoc.field("title");
            where.setDisplayName(topicTitle);
        }
        if(postNames.contains(className)) {
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
        /**
        if("EdgePostMessageComment".equals(className)) {
            activity.setType("PostMessageComment");

            object.setDisplayName("comment");
            object.setObjectType("comment");

            ODocument messageDoc = objectDoc.field("postMessage");
            ODocument targetDoc = messageDoc.field("post");
            target.setId(OrientIdentityUtil.encode(targetDoc.getIdentity().toString()));

            String targetTitle = targetDoc.field("title", String.class);
            target.setDisplayName(targetTitle);

            String targetType = targetDoc.field("type", String.class);
            target.setObjectType(targetType);


        }
        if("EdgeQuestion".equals(className)) {
            //object is a question, target is a topic

            String title = objectDoc.field("title");
            object.setDisplayName(title);

            String type = objectDoc.field("type");
            object.setObjectType(type);

            activity.setType("Question");
        }
        if("EdgeQuestionComment".equals(className)) {
            activity.setType("QuestionComment");

            object.setDisplayName("comment");
            object.setObjectType("comment");

            ODocument targetDoc = objectDoc.field("question");
            target.setId(OrientIdentityUtil.encode(targetDoc.getIdentity().toString()));

            String targetTitle = targetDoc.field("title", String.class);
            target.setDisplayName(targetTitle);
            target.setObjectType("question");
        }
        if("EdgeAnswer".equals(className)) {
            activity.setType("Answer");

            object.setDisplayName("answer");

            object.setObjectType("answer");

            ODocument targetDoc = objectDoc.field("question");
            target.setId(OrientIdentityUtil.encode(targetDoc.getIdentity().toString()));

            String targetTitle = targetDoc.field("title", String.class);
            target.setDisplayName(targetTitle);
            target.setObjectType("question");
        }
        if("EdgeAnswerComment".equals(className)) {
            activity.setType("AnswerComment");

            object.setDisplayName("comment");
            object.setObjectType("comment");

            ODocument answerDoc = objectDoc.field("answer");
            ODocument targetDoc = answerDoc.field("question");
            target.setId(OrientIdentityUtil.encode(targetDoc.getIdentity().toString()));

            String targetTitle = targetDoc.field("title", String.class);
            target.setDisplayName(targetTitle);

            target.setObjectType("question");
        }
*/

        return activity;
    }

}

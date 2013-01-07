package com.tissue.core.converter;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.domain.social.Activity;
import com.tissue.domain.social.ActivityObject;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

public class ActivityConverter {
    private static List<String> planNames = Arrays.asList("EdgeHost", "EdgeJoin");
    private static List<String> postNames = Arrays.asList("EdgePost", "EdgeQuestion");
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

        ActivityObject actor = new ActivityObject();
        activity.setActor(actor);

        ActivityObject object = new ActivityObject();
        activity.setObject(object);

        ActivityObject target = new ActivityObject();
        activity.setTarget(target);

        Date published = doc.field("createTime", Date.class);
        activity.setPublished(published);

        ODocument userDoc = doc.field("out");
        actor.setId(OrientIdentityUtil.encode(userDoc.getIdentity().toString()));
        String displayName = userDoc.field("displayName", String.class);
        actor.setDisplayName(displayName);
        actor.setObjectType("person");

        ODocument objectDoc = doc.field("in");
        object.setId(OrientIdentityUtil.encode(objectDoc.getIdentity().toString()));

        if("EdgeCreate".equals(className)) {
            //object is a topic, no target need to be set

            String title = objectDoc.field("title");
            object.setDisplayName(title);

            object.setObjectType("topic");

            activity.setType("Topic");
        }
        if("EdgeHost".equals(className)) {
            //object is a plan, target is a topic

            object.setDisplayName("plan");
            object.setObjectType("plan");

            ODocument targetDoc = objectDoc.field("topic");
            target.setId(OrientIdentityUtil.encode(targetDoc.getIdentity().toString()));

            String title = targetDoc.field("title");
            target.setDisplayName(title);

            activity.setType("Host");

        }
        if("EdgeJoin".equals(className)) {
            //object is a plan, target is a topic

            object.setDisplayName("plan");
            object.setObjectType("plan");

            ODocument targetDoc = objectDoc.field("topic");
            target.setId(OrientIdentityUtil.encode(targetDoc.getIdentity().toString()));

            String title = targetDoc.field("title");
            target.setDisplayName(title);

            activity.setType("Join");
        }
        if("EdgePost".equals(className)) {
            //object is a post(concept, note, tutorial), target is a topic

            String title = objectDoc.field("title");
            object.setDisplayName(title);

            String type = objectDoc.field("type");
            object.setObjectType(type);

            activity.setType("Post");
        }
        if("EdgePostMessage".equals(className)) {

            activity.setType("PostMessage");

            //object is a postmessage, target is a post

            String title = objectDoc.field("title");
            object.setDisplayName(title);

            object.setObjectType("post");

            ODocument targetDoc = objectDoc.field("post");
            target.setId(OrientIdentityUtil.encode(targetDoc.getIdentity().toString()));

            String targetTitle = targetDoc.field("title", String.class);
            target.setDisplayName(targetTitle);

            String targetType = targetDoc.field("type", String.class);
            target.setObjectType(targetType);

        }
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

        return activity;
    }

}

package com.tissue.core.converter;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.domain.profile.User;
import com.tissue.domain.social.Event;
import com.tissue.domain.plan.Topic;
import com.tissue.domain.plan.Plan;
import com.tissue.domain.plan.Post;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class EventConverter {

    public static Event buildEvent(ODocument eventDoc) {
        if(eventDoc == null) {
            return null;
        }

        Date published = eventDoc.field("published", Date.class);
        String type = eventDoc.field("type", String.class);
        System.out.println(type);

        //ODocument userDoc = eventDoc.field("actor");
        //User actor = UserConverter.buildUser(userDoc);

        ODocument objectDoc = eventDoc.field("object");
        switch(type) {
            case "topic": 
                Topic topic = TopicConverter.buildMiniumTopic(objectDoc);
                System.out.println("<<<" + topic.getTitle());
                break;
            case "plan": 
                Plan plan = PlanConverter.buildPlan(objectDoc);
                System.out.println("<<<" + plan.getDuration());
                break;
            case "post":
                Post post = PostConverter.buildPost(objectDoc);
                break;
            default:
                System.out.println("do nothing");
                break;
        }

        Event event = new Event();
        event.setId(OrientIdentityUtil.encode(eventDoc.getIdentity().toString()));
        event.setPublished(published);
        System.out.println("event build...");
        return event;
    }

    public static List<User> buildMembers(Set<ODocument> membersDoc) {
        if(membersDoc == null) {
            return null;
        }

        List<User> members = new ArrayList();

        for(ODocument memberDoc : membersDoc) {
            User user = UserConverter.buildUser(memberDoc);
            members.add(user);
        }
        return members;
    }



}

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
import java.util.HashSet;
import java.util.Map;

public class EventConverter {

    public static ODocument convertEvent(Event event) {
        ODocument eventDoc = new ODocument("Event");
        eventDoc.field("published", event.getPublished());
        eventDoc.field("type", event.getType());
        eventDoc.field("actor", new ORecordId(OrientIdentityUtil.decode(event.getActor().getId())));
        eventDoc.field("object", event.getObject());
        eventDoc.field("target", event.getTarget());

        List<User> users = event.getNotifies();
        if(users != null) {
            Set<ORecordId> notifiesDoc = new HashSet();
            for(User user : users) {
                String ridUser = OrientIdentityUtil.decode(user.getId());
                notifiesDoc.add(new ORecordId(ridUser));
            }
            eventDoc.field("notifies", notifiesDoc);
        }

        return eventDoc;
    }

    public static List<Event> buildEvents(List<ODocument> eventsDoc) {
        List<Event> events = new ArrayList();
        if(eventsDoc != null) {
            for(ODocument eventDoc : eventsDoc) {
                Event event = buildEvent(eventDoc);
                events.add(event);
            }
        }
        return events;
    }

    public static Event buildEvent(ODocument eventDoc) {
        if(eventDoc == null) {
            return null;
        }

        Date published = eventDoc.field("published", Date.class);
        String type = eventDoc.field("type", String.class);

        ODocument userDoc = eventDoc.field("actor");
        User actor = UserConverter.buildUser(userDoc);

        Map object = eventDoc.field("object", Map.class);
        Map target = eventDoc.field("target", Map.class);

        Event event = new Event();
        event.setId(OrientIdentityUtil.encode(eventDoc.getIdentity().toString()));
        event.setPublished(published);
        event.setType(type);
        event.setActor(actor);
        event.setObject(object);
        if(target != null) {
            event.setTarget(target);
        }
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

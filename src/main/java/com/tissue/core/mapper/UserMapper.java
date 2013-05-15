package com.tissue.core.mapper;

import com.tissue.core.User;
import com.tissue.core.command.UserCommand;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import com.tinkerpop.blueprints.Vertex;

import java.util.Date;

public class UserMapper {

    public static ODocument convertUser(UserCommand command) {
        ODocument doc = new ODocument("User");
        doc.field("displayName", command.getDisplayName());
        doc.field("headline", command.getHeadline());
        doc.field("inviteLimit", 32);
        doc.field("status", command.getStatus());
        return doc;
    }

    public static User buildUser(Vertex v) {
        User user = new User();
        user.setId(v.getId().toString());

        String displayName = v.getProperty("displayName");
        user.setDisplayName(displayName);

        String headline = v.getProperty("headline");
        user.setHeadline(headline);

        Integer inviteLimit = v.getProperty("inviteLimit");
        user.setInviteLimit(inviteLimit);

        /**
        String status = v.getProperty("status");
        user.setStatus(status);
        */

        return user;
    }

    public static User buildUser(ODocument doc) {
        User user = new User();
        String rid = doc.getIdentity().toString();
        user.setId(rid);

        String displayName = doc.field("displayName", String.class);
        user.setDisplayName(displayName);

        String headline = doc.field("headline", String.class);
        user.setHeadline(headline);

        Integer inviteLimit = doc.field("inviteLimit", Integer.class);
        user.setInviteLimit(inviteLimit);

        /**
        String status = doc.field("status", String.class);
        user.setStatus(status);
        */

        return user;
    }

}

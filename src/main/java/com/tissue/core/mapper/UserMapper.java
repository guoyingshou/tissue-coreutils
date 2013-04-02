package com.tissue.core.mapper;

import com.tissue.core.User;
import com.tissue.core.command.UserCommand;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;

public class UserMapper {

    public static ODocument convertUser(UserCommand user) {
        ODocument doc = new ODocument("User");
        doc.field("displayName", user.getDisplayName());
        doc.field("headline", user.getHeadline());
        doc.field("createTime", new Date());
        doc.field("inviteLimit", 32);
        return doc;
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

        return user;
    }

}

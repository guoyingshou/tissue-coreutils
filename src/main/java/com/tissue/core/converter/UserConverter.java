package com.tissue.core.converter;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.domain.profile.User;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class UserConverter {

    public static User buildUser(ODocument userDoc) {
        if(userDoc == null) {
            return null;
        }

        User user = new User();
        user.setId(OrientIdentityUtil.encode(userDoc.getIdentity().toString()));

        String displayName = userDoc.field("displayName", String.class);
        user.setDisplayName(displayName);

        return user;
    }

    public static List<User> buildMembers(Set<ODocument> membersDoc) {
        if(membersDoc == null) {
            return null;
        }

        List<User> members = new ArrayList();

        for(ODocument memberDoc : membersDoc) {
            User user = buildUser(memberDoc);
            members.add(user);
        }
        return members;
    }

}

package com.tissue.core.mapper;

import com.tissue.core.security.UserDetailsImpl;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class UserDetailsMapper {

    public static UserDetailsImpl buildUser(ODocument doc) {

        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setId(doc.getIdentity().toString());

        String username = doc.field("username", String.class);
        userDetails.setUsername(username);

        String password = doc.field("password", String.class);
        userDetails.setPassword(password);

        String displayName = doc.field("displayName", String.class);
        userDetails.setDisplayName(displayName);

        Set<String> roles = doc.field("roles", Set.class);
        userDetails.setRoles(roles);

        Integer inviteLimit = doc.field("inviteLimit", Integer.class);
        userDetails.setInviteLimit(inviteLimit);

        return userDetails;
    }

}

package com.tissue.security.mapper;

import com.tissue.security.UserDetailsImpl;

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

        Set<String> roles = doc.field("roles", Set.class);
        userDetails.setRoles(roles);

        return userDetails;
    }

}

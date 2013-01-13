package com.tissue.core.mapper;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.security.UserDetailsImpl;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class UserDetailsMapper {

    public static UserDetailsImpl buildUser(ODocument userDetailsDoc) {

        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setId(OrientIdentityUtil.encode(userDetailsDoc.getIdentity().toString()));

        String username = userDetailsDoc.field("username", String.class);
        userDetails.setUsername(username);

        String password = userDetailsDoc.field("password", String.class);
        userDetails.setPassword(password);

        String displayName = userDetailsDoc.field("displayName", String.class);
        userDetails.setDisplayName(displayName);

        return userDetails;
    }

}

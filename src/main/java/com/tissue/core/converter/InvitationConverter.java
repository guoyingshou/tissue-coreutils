package com.tissue.core.converter;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.domain.profile.User;
import com.tissue.domain.profile.Invitation;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class InvitationConverter {

    public static Invitation buildInvitation(ODocument invitationDoc) {
        if(invitationDoc == null) {
            return null;
        }

        String content = invitationDoc.field("content", String.class);
        Date createTime = invitationDoc.field("createTime", Date.class);

        ODocument userDoc = invitationDoc.field("out");
        User user = UserConverter.buildUser(userDoc);

        Invitation invitation = new Invitation();
        invitation.setId(OrientIdentityUtil.encode(invitationDoc.getIdentity().toString()));
        invitation.setContent(content);
        invitation.setCreateTime(createTime);
        invitation.setInvitor(user);

        return invitation;
    }
}

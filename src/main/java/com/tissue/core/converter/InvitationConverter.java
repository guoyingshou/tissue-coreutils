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
        String content = invitationDoc.field("content", String.class);
        Date createTime = invitationDoc.field("createTime", Date.class);
        Date updateTime = invitationDoc.field("updateTime", Date.class);

        ODocument userOutDoc = invitationDoc.field("out");
        User invitor = UserConverter.buildUser(userOutDoc);

        ODocument userInDoc = invitationDoc.field("in");
        User invitee = UserConverter.buildUser(userInDoc);

        Invitation invitation = new Invitation();
        invitation.setId(OrientIdentityUtil.encode(invitationDoc.getIdentity().toString()));
        invitation.setContent(content);
        invitation.setCreateTime(createTime);
        invitation.setUpdateTime(updateTime);
        invitation.setInvitor(invitor);
        invitation.setInvitee(invitee);

        return invitation;
    }
}

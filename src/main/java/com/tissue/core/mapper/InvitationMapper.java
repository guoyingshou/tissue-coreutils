package com.tissue.core.mapper;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.social.User;
import com.tissue.core.social.Invitation;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class InvitationMapper {

    public static Invitation buildInvitation(ODocument invitationDoc) {

        Invitation invitation = new Invitation();
        invitation.setId(OrientIdentityUtil.encode(invitationDoc.getIdentity().toString()));

        String status = invitationDoc.field("status", String.class);
        invitation.setStatus(status);

        String content = invitationDoc.field("content", String.class);
        invitation.setContent(content);

        Date createTime = invitationDoc.field("createTime", Date.class);
        invitation.setCreateTime(createTime);

        ODocument userOutDoc = invitationDoc.field("out");
        User invitor = UserMapper.buildUser(userOutDoc);
        invitation.setInvitor(invitor);

        ODocument userInDoc = invitationDoc.field("in");
        User invitee = UserMapper.buildUser(userInDoc);
        invitation.setInvitee(invitee);

        return invitation;
    }
}

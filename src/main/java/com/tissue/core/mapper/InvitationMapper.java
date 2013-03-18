package com.tissue.core.mapper;

import com.tissue.core.User;
import com.tissue.core.Account;
import com.tissue.core.social.Invitation;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.Date;

public class InvitationMapper {

    public static Invitation buildInvitation(ODocument doc) {

        //out is an account
        ODocument fromDoc = doc.field("out");
        Account from = AccountMapper.buildAccount(fromDoc);

        //"in" is a user
        ODocument toUserDoc = doc.field("in");
        User to = UserMapper.buildUser(toUserDoc);

        String content = doc.field("content", String.class);
        Date createTime = doc.field("createTime", Date.class);

        Invitation invitation = new Invitation();
        invitation.setId(doc.getIdentity().toString());
        invitation.setFrom(from);
        invitation.setTo(to);
        invitation.setContent(content);
        invitation.setCreateTime(createTime);

        return invitation;
    }

}

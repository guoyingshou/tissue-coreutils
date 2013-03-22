package com.tissue.social.mapper;

import com.tissue.core.User;
import com.tissue.core.Account;
import com.tissue.core.mapper.UserMapper;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.social.Invitation;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.Date;

public class InvitationMapper {

    public static Invitation buildInvitation(ODocument doc) {

        Invitation invitation = new Invitation();
        invitation.setId(doc.getIdentity().toString());

        String content = doc.field("content", String.class);
        invitation.setContent(content);

        Date createTime = doc.field("createTime", Date.class);
        invitation.setCreateTime(createTime);

        //out is an account
        ODocument accountDoc = doc.field("out");
        Account account = AccountMapper.buildAccount(accountDoc);
        invitation.setAccount(account);

        /**
        //"in" is a user
        ODocument toUserDoc = doc.field("in");
        User to = UserMapper.buildUser(toUserDoc);
        invitation.setTo(to);
        */

        return invitation;
    }

}

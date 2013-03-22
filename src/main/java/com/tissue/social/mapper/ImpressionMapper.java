package com.tissue.social.mapper;

import com.tissue.core.Account;
import com.tissue.core.User;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.core.mapper.UserMapper;
import com.tissue.social.Impression;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.Set;

public class ImpressionMapper {

    public static Impression buildImpression(ODocument doc) {

        Impression impression = new Impression();
        impression.setId(doc.getIdentity().toString());

        String content = doc.field("content", String.class);
        impression.setContent(content);

        Date createTime = doc.field("createTime", Date.class);
        impression.setCreateTime(createTime);

        //out is an account
        ODocument accountDoc = doc.field("out");
        Account account = AccountMapper.buildAccount(accountDoc);
        impression.setAccount(account);

        /**
        //"in" is a user
        ODocument toUserDoc = doc.field("in");
        User to = UserMapper.buildUser(toUserDoc);
        impression.setTo(to);
        */

        return impression;
    }

}

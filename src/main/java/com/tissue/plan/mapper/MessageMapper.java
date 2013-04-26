package com.tissue.plan.mapper;

import com.tissue.core.Account;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.plan.command.MessageCommand;
import com.tissue.plan.Message;
import com.tissue.plan.MessageReply;
import com.tissue.plan.Article;

import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class MessageMapper {

    public static ODocument convertMessage(MessageCommand command) {
        ODocument doc = new ODocument("Message");
        doc.field("content", command.getContent());
        return doc;
    }

    public static Message buildMessage(ODocument doc) {
        Message message = new Message();
        message.setId(doc.getIdentity().toString());

        String content = doc.field("content", String.class);
        message.setContent(content);

        /**
        Set<ODocument> edgeCreatePostDocs = doc.field("in");
        for(ODocument edgeCreatePostDoc : edgeCreatePostDocs) {
            Date createTime = edgeCreatePostDoc.field("createTime", Date.class);
            message.setCreateTime(createTime);

            ODocument accountDoc = edgeCreatePostDoc.field("out");
            Account account = AccountMapper.buildAccount(accountDoc);
            message.setAccount(account);
            break;
        }
        */

        return message;
    }

}

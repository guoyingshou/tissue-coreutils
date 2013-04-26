package com.tissue.plan.mapper;

import com.tissue.core.Account;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.plan.command.MessageReplyCommand;
import com.tissue.plan.MessageReply;
import com.tissue.plan.Message;
import com.tissue.plan.Post;

import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class MessageReplyMapper {

    public static ODocument convertMessageReply(MessageReplyCommand command) {
        ODocument commentDoc = new ODocument("MessageReply");
        commentDoc.field("content", command.getContent());
        return commentDoc;
    }

    public static MessageReply buildMessageReply(ODocument doc) {

        MessageReply reply = new MessageReply();
        reply.setId(doc.getIdentity().toString());
       
        String content = doc.field("content", String.class);
        reply.setContent(content);

        /**
        Set<ODocument> edgeCreatePostDocs = doc.field("in");
        for(ODocument edgeCreatePostDoc : edgeCreatePostDocs) {
            Date createTime = edgeCreatePostDoc.field("createTime", Date.class);
            reply.setCreateTime(createTime);

            ODocument accountDoc = edgeCreatePostDoc.field("out");
            Account account = AccountMapper.buildAccount(accountDoc);
            reply.setAccount(account);
            break;
        }
        */

        return reply;
    }

}

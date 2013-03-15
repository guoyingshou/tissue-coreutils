package com.tissue.core.mapper;

import com.tissue.core.Account;
import com.tissue.core.command.MessageReplyCommand;
import com.tissue.core.plan.MessageReply;
import com.tissue.core.plan.Message;
import com.tissue.core.plan.Post;

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

        Set<ODocument> inEdges = doc.field("in");
        for(ODocument inEdge : inEdges) {
            Date createTime = inEdge.field("createTime", Date.class);
            reply.setCreateTime(createTime);

            ODocument accountDoc = inEdge.field("out");
            Account account = AccountMapper.buildAccount(accountDoc);
            reply.setAccount(account);
            break;
        }

        return reply;
    }

}

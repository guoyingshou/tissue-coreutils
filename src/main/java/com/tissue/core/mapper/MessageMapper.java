package com.tissue.core.mapper;

import com.tissue.core.command.MessageCommand;
import com.tissue.core.social.Account;
import com.tissue.core.plan.Message;
import com.tissue.core.plan.MessageReply;
import com.tissue.core.plan.Article;

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

        Set<ODocument> inEdges = doc.field("in");
        for(ODocument inEdge : inEdges) {
            Date createTime = inEdge.field("createTime", Date.class);
            message.setCreateTime(createTime);

            ODocument accountDoc = inEdge.field("out");
            Account account = AccountMapper.buildAccount(accountDoc);
            message.setAccount(account);
            break;
        }

        ODocument articleDoc = doc.field("article");
        Article article = ArticleMapper.buildArticle(articleDoc);
        message.setArticle(article);

        return message;
    }

    public static Message buildMessageDetails(ODocument doc) {

        Message message = buildMessage(doc);

        List<ODocument> commentsDoc = doc.field("comments");
        if(commentsDoc != null) {
            for(ODocument commentDoc : commentsDoc) {
                String deleted = commentDoc.field("deleted", String.class);
                if(deleted == null) {
                    MessageReply reply = MessageReplyMapper.buildMessageReply(commentDoc);
                    message.addReply(reply);
                }
            }
        }
        return message;
    }
}

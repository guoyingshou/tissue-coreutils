package com.tissue.core.mapper;

import com.tissue.core.command.ArticleCommand;
import com.tissue.core.social.Account;
import com.tissue.core.plan.Article;
import com.tissue.core.plan.Message;
import com.tissue.core.plan.Plan;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.record.OTrackedList;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class ArticleMapper {

    public static ODocument convertArticle(ArticleCommand command) {
        ODocument doc = new ODocument("Article");
        doc.field("title", command.getTitle());
        doc.field("content", command.getContent());
        doc.field("type", command.getType());
        doc.field("createTime", new Date());
        return doc;
    }

    public static Article buildArticle(ODocument doc) {
        Article article = new Article();
        article.setId(doc.getIdentity().toString());

        String title = doc.field("title", String.class);
        article.setTitle(title);

        String content = doc.field("content", String.class);
        article.setContent(content);

        String type = doc.field("type", String.class);
        article.setType(type);

        Date createTime = doc.field("createTime", Date.class);
        article.setCreateTime(createTime);

        Boolean deleted = doc.field("deleted", Boolean.class);
        if(deleted != null) {
            article.setDeleted(deleted);
        }
 
        //creator
        Set<ODocument> inEdgesDoc = doc.field("in");
        for(ODocument inEdgeDoc : inEdgesDoc) {
            ODocument accountDoc = inEdgeDoc.field("out");
            Account account = AccountMapper.buildAccount(accountDoc);
            article.setAccount(account);
        }

        return article;
    }

    public static Article buildArticleDetails(ODocument postDoc) {
        Article article = buildArticle(postDoc);

        List<ODocument> messagesDoc = postDoc.field("messages");
        if(messagesDoc != null) {
            for(ODocument messageDoc : messagesDoc) {
                String deleted = messageDoc.field("deleted", String.class);
                if(deleted == null) {
                    Message message = MessageMapper.buildMessageDetails(messageDoc);
                    article.addMessage(message);
                }
            }
        }
        return article;
    }
}

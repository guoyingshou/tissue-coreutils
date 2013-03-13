package com.tissue.core.mapper;

import com.tissue.core.social.Account;
//import com.tissue.core.plan.Post;
import com.tissue.core.plan.Article;
import com.tissue.core.plan.PostMessage;
import com.tissue.core.plan.Plan;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.record.OTrackedList;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class ArticleMapper {

    public static Article buildArticleSelf(ODocument doc) {
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
 
        return article;
    }

    public static Article buildArticle(ODocument doc) {
        Article article = buildArticleSelf(doc);

        ODocument planDoc = doc.field("plan");
        Plan plan = PlanMapper.buildPlanDetails(planDoc);
        article.setPlan(plan);
 
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
                    PostMessage postMessage = PostMessageMapper.buildPostMessageDetails(messageDoc);
                    article.addPostMessage(postMessage);
                }
            }
        }
        return article;
    }
}

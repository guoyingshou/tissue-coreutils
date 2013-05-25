package com.tissue.plan.mapper;

import com.tissue.core.Account;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.plan.command.PostCommand;
import com.tissue.plan.Article;
import com.tissue.plan.Message;
import com.tissue.plan.Plan;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.record.OTrackedList;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class ArticleMapper {

    public static Article buildArticle(ODocument doc) {
        Article article = new Article();
        article.setId(doc.getIdentity().toString());

        String title = doc.field("title", String.class);
        article.setTitle(title);

        String content = doc.field("content", String.class);
        article.setContent(content);

        String type = doc.field("type", String.class);
        article.setType(type);

        Boolean deleted = doc.field("deleted", Boolean.class);
        if(deleted != null) {
            article.setDeleted(deleted);
        }

        AccountMapper.setAccount(article, doc);
 
        return article;
    }

    /**
    public static void setAccount(Article article, ODocument doc) {
        Date createTime = doc.field("out_PostAccount.createTime", Date.class);
        article.setCreateTime(createTime);

        ODocument accountDoc = doc.field("out_PostAccount.in");
        Account account = AccountMapper.buildAccount(accountDoc);

        article.setAccount(account);
    }
    */
}

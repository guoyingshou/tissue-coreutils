package com.tissue.plan.mapper;

import com.tissue.core.Account;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.plan.command.PostCommand;
import com.tissue.plan.Post;
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

public class PostMapper {

    public static ODocument convertPost(PostCommand command) {
        ODocument doc = new ODocument(command.getType());
        doc.field("title", command.getTitle());
        doc.field("content", command.getContent());
        doc.field("type", command.getType());
        doc.field("createTime", new Date());
        return doc;
    }

    public static Post buildPost(ODocument doc) {
        Post post = new Post();
        post.setId(doc.getIdentity().toString());

        String postTitle = doc.field("title", String.class);
        post.setTitle(postTitle);

        String postContent = doc.field("content", String.class);
        post.setContent(postContent);

        String postType = doc.field("type", String.class);
        post.setType(postType);

        Date createTime = doc.field("createTime", Date.class);
        post.setCreateTime(createTime);

        Boolean deleted = doc.field("deleted", Boolean.class);
        if(deleted != null) {
            post.setDeleted(deleted);
        }
 
        //creator
        Set<ODocument> inEdgesDoc = doc.field("in");
        for(ODocument inEdgeDoc : inEdgesDoc) {
            ODocument accountDoc = inEdgeDoc.field("out");
            Account account = AccountMapper.buildAccount(accountDoc);
            post.setAccount(account);
            break;
        }

        return post;
    }

}

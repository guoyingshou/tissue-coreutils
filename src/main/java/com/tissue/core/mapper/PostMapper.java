package com.tissue.core.mapper;

import com.tissue.core.command.PostCommand;
import com.tissue.core.social.Account;
import com.tissue.core.plan.Post;
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

public class PostMapper {

    public static ODocument convert(PostCommand command) {
        ODocument doc = new ODocument(command.getType());
        doc.field("title", command.getTitle());
        doc.field("content", command.getContent());
        doc.field("type", command.getType());
        doc.field("createTime", new Date());
        return doc;
    }

    public static Post buildPostSelf(ODocument doc) {
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
 
        return post;
    }

    public static Post buildPost(ODocument doc) {
        Post post = buildPostSelf(doc);

        Set<ODocument> inEdgesDoc = doc.field("in");
        for(ODocument inEdgeDoc : inEdgesDoc) {
            ODocument accountDoc = inEdgeDoc.field("out");
            Account account = AccountMapper.buildAccount(accountDoc);
            post.setAccount(account);
            break;
        }
        return post;
    }

    /**
    public static Post buildPostDetails(ODocument postDoc) {
        Post post = buildPost(postDoc);

        List<ODocument> messagesDoc = postDoc.field("messages");
        if(messagesDoc != null) {
            for(ODocument messageDoc : messagesDoc) {
                String deleted = messageDoc.field("deleted", String.class);
                if(deleted == null) {
                    PostMessage postMessage = PostMessageMapper.buildPostMessageDetails(messageDoc);
                    post.addPostMessage(postMessage);
                }
            }
        }
        return post;
    }
    */
}

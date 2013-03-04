package com.tissue.core.mapper;

import com.tissue.core.command.PostMessageCommand;
import com.tissue.core.social.Account;
import com.tissue.core.plan.PostMessage;
import com.tissue.core.plan.PostMessageComment;
import com.tissue.core.plan.Post;

import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class PostMessageMapper {

    public static ODocument convertPostMessage(PostMessageCommand postMessage) {
        ODocument doc = new ODocument("PostMessage");
        doc.field("content", postMessage.getContent());
        return doc;
    }

    public static PostMessage buildPostMessage(ODocument messageDoc) {

        PostMessage message = new PostMessage();
        message.setId(messageDoc.getIdentity().toString());

        String messageContent = messageDoc.field("content", String.class);
        message.setContent(messageContent);

        Set<ODocument> inEdges = messageDoc.field("in");
        for(ODocument inEdge : inEdges) {
            String label = inEdge.field("label", String.class);
            if("postMessage".equals(label)) {
                Date createTime = inEdge.field("createTime", Date.class);
                message.setCreateTime(createTime);

                ODocument accountDoc = inEdge.field("out");
                Account account = AccountMapper.buildAccount(accountDoc);
                message.setAccount(account);
                break;
            }
        }
        ODocument postDoc = messageDoc.field("post");
        Post post = PostMapper.buildPost(postDoc);
        message.setPost(post);

        return message;
    }

    public static PostMessage buildPostMessageDetails(ODocument messageDoc) {

        PostMessage message = buildPostMessage(messageDoc);

        List<ODocument> commentsDoc = messageDoc.field("comments");
        if(commentsDoc != null) {
            for(ODocument commentDoc : commentsDoc) {
                String deleted = commentDoc.field("deleted", String.class);
                if(deleted == null) {
                    PostMessageComment comment = PostMessageCommentMapper.buildPostMessageComment(commentDoc);
                    message.addComment(comment);
                }
            }
        }
        return message;
    }
}

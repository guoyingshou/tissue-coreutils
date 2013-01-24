package com.tissue.core.mapper;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.social.User;
import com.tissue.core.plan.PostMessage;
import com.tissue.core.plan.PostMessageComment;
import com.tissue.core.plan.Post;

import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class PostMessageMapper {

    public static ODocument convertPostMessage(PostMessage postMessage) {
        ODocument doc = new ODocument("PostMessage");
        doc.field("content", postMessage.getContent());
        return doc;
    }

    public static PostMessage buildPostMessage(ODocument messageDoc) {

        PostMessage message = new PostMessage();
        message.setId(OrientIdentityUtil.encode(messageDoc.getIdentity().toString()));

        String messageContent = messageDoc.field("content", String.class);
        message.setContent(messageContent);

        Set<ODocument> inEdges = messageDoc.field("in");
        for(ODocument inEdge : inEdges) {
            String label = inEdge.field("label", String.class);
            if("postMessage".equals(label)) {
                Date createTime = inEdge.field("createTime", Date.class);
                message.setCreateTime(createTime);

                ODocument userDoc = inEdge.field("out");
                User user = UserMapper.buildUserSelf(userDoc);
                message.setUser(user);
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
                String status = commentDoc.field("status", String.class);
                if(status == null) {
                    PostMessageComment comment = PostMessageCommentMapper.buildPostMessageComment(commentDoc);
                    message.addComment(comment);
                }
            }
        }
        return message;
    }
}

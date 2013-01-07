package com.tissue.core.converter;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.converter.PostMessageCommentConverter;

import com.tissue.domain.profile.User;
import com.tissue.domain.plan.PostMessage;
import com.tissue.domain.plan.PostMessageComment;
import com.tissue.domain.plan.Post;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class PostMessageConverter {

    public static ODocument convertPostMessage(PostMessage postMessage) {
        ODocument doc = new ODocument("PostMessage");
        doc.field("content", postMessage.getContent());
        doc.field("post", new ORecordId(OrientIdentityUtil.decode(postMessage.getPost().getId())));

        return doc;
    }

    public static PostMessage buildPostMessage(ODocument messageDoc) {

        PostMessage message = buildPostMessageWithoutChild(messageDoc);

        Set<ODocument> commentsDoc = messageDoc.field("comments");
        if(commentsDoc != null) {
            List<PostMessageComment> messageComments = PostMessageCommentConverter.buildPostMessageComments(commentsDoc);
            message.setComments(messageComments); 
        }
 
        return message;
    }

    public static PostMessage buildPostMessageWithoutChild(ODocument messageDoc) {

        PostMessage message = new PostMessage();
        message.setId(OrientIdentityUtil.encode(messageDoc.getIdentity().toString()));

        String messageContent = messageDoc.field("content", String.class);
        message.setContent(messageContent);

        Set<ODocument> inEdges = messageDoc.field("in");
        if(inEdges != null) {
            for(ODocument inEdge : inEdges) {
                //if(inEdge.field("target").equals("postMessage")) {
                if("EdgePostMessage".equals(inEdge.getClassName())) {
                    Date createTime = inEdge.field("createTime", Date.class);
                    message.setCreateTime(createTime);

                    ODocument userDoc = inEdge.field("out");
                    User user = UserConverter.buildUser(userDoc);
                    message.setUser(user);
                    break;
                }
            }
        }

        ODocument postDoc = messageDoc.field("post");
        Post post = PostConverter.buildPostWithoutChild(postDoc);
        message.setPost(post);

        return message;
    }


    public static List<PostMessage> buildPostMessages(Set<ODocument> messagesDoc) {
        List<PostMessage> messages = new ArrayList();
        for(ODocument messageDoc : messagesDoc) {
            PostMessage message = buildPostMessage(messageDoc);
            messages.add(message);
        }
        return messages;
    }

}

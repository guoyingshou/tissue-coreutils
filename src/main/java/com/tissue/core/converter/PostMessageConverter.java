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
        doc.field("createTime", postMessage.getCreateTime());
        doc.field("user", new ORecordId(OrientIdentityUtil.decode(postMessage.getUser().getId())));
        doc.field("post", new ORecordId(OrientIdentityUtil.decode(postMessage.getPost().getId())));

        return doc;
    }

    public static PostMessage buildPostMessage(ODocument messageDoc) {
        String messageContent = messageDoc.field("content", String.class);
        Date messageCreateTime = messageDoc.field("createTime", Date.class);

        ODocument userDoc = messageDoc.field("user");
        User user = UserConverter.buildUser(userDoc);

        List<PostMessageComment> messageComments = null;
        Set<ODocument> commentsDoc = messageDoc.field("comments");
        if(commentsDoc != null) {
            messageComments = PostMessageCommentConverter.buildPostMessageComments(commentsDoc);
        }

        PostMessage message = new PostMessage();
        message.setId(OrientIdentityUtil.encode(messageDoc.getIdentity().toString()));
        message.setContent(messageContent);
        message.setCreateTime(messageCreateTime);
        message.setUser(user);
        if(messageComments != null) {
            message.setComments(messageComments); 
        }
 
        return message;
    }

    public static PostMessage buildPostMessageWithoutChild(ODocument messageDoc) {
        String messageContent = messageDoc.field("content", String.class);
        Date messageCreateTime = messageDoc.field("createTime", Date.class);

        ODocument userDoc = messageDoc.field("user");
        User user = UserConverter.buildUser(userDoc);

        ODocument postDoc = messageDoc.field("post");
        Post post = PostConverter.buildPostWithoutChild(postDoc);

        PostMessage message = new PostMessage();
        message.setId(OrientIdentityUtil.encode(messageDoc.getIdentity().toString()));
        message.setContent(messageContent);
        message.setCreateTime(messageCreateTime);
        message.setUser(user);
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

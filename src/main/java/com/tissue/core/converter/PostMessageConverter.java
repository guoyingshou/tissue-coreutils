package com.tissue.core.converter;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.domain.profile.User;
import com.tissue.domain.plan.PostMessage;
import com.tissue.domain.plan.PostMessageComment;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class PostMessageConverter {

    public static PostMessage buildMessage(ODocument messageDoc) {
        String messageContent = messageDoc.field("content", String.class);
        Date messageCreateTime = messageDoc.field("createTime", Date.class);

        PostMessage message = new PostMessage();
        message.setId(OrientIdentityUtil.encode(messageDoc.getIdentity().toString()));
        message.setContent(messageContent);
        message.setCreateTime(messageCreateTime);

        Set<ODocument> commentsDoc = messageDoc.field("comments");
        List<PostMessageComment> messageComments = PostMessageCommentConverter.buildMessageComments(commentsDoc);
        if(messageComments != null) {
            message.setComments(messageComments); 
        }
 
        return message;
    }

    public static List<PostMessage> buildMessages(Set<ODocument> messagesDoc) {
        if(messagesDoc == null) {
            return null;
        }

        List<PostMessage> messages = new ArrayList();
        for(ODocument messageDoc : messagesDoc) {
            PostMessage message = buildMessage(messageDoc);
            messages.add(message);
        }
        return messages;
    }

}

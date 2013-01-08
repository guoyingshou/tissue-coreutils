package com.tissue.core.converter;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.converter.PostMessageCommentConverter;
import com.tissue.core.converter.PostMessageConverter;

import com.tissue.domain.profile.User;
import com.tissue.domain.plan.PostMessageComment;
import com.tissue.domain.plan.PostMessage;
import com.tissue.domain.plan.Post;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class PostMessageCommentConverter {

    public static ODocument convertPostMessageComment(PostMessageComment postMessageComment) {
        ODocument commentDoc = new ODocument("PostMessageComment");
        commentDoc.field("content", postMessageComment.getContent());
        commentDoc.field("postMessage", new ORecordId(OrientIdentityUtil.decode(postMessageComment.getPostMessage().getId())));
        return commentDoc;
    }

    public static PostMessageComment buildPostMessageComment(ODocument commentDoc) {

        PostMessageComment messageComment = new PostMessageComment();
        messageComment.setId(OrientIdentityUtil.encode(commentDoc.getIdentity().toString()));
       
        String commentContent = commentDoc.field("content", String.class);
        messageComment.setContent(commentContent);

        Set<ODocument> inEdges = commentDoc.field("in");
        for(ODocument inEdge : inEdges) {
            if("EdgePostMessageComment".equals(inEdge.getClassName())) {
                Date createTime = inEdge.field("createTime", Date.class);
                messageComment.setCreateTime(createTime);

                ODocument userDoc = inEdge.field("out");
                User user = UserConverter.buildUser(userDoc);
                messageComment.setUser(user);
                break;
            }
        }

        return messageComment;
    }

    public static List<PostMessageComment> buildPostMessageComments(Set<ODocument> commentsDoc) {
        List<PostMessageComment> messageComments = new ArrayList();

        for(ODocument commentDoc : commentsDoc) {
            String status = commentDoc.field("status", String.class);
            if(status == null) {
                PostMessageComment messageComment = buildPostMessageComment(commentDoc);
                messageComments.add(messageComment);
            }
        }

        return messageComments;
    }

    public static PostMessageComment buildPostMessageCommentWithParent(ODocument commentDoc) {
        PostMessageComment messageComment = buildPostMessageComment(commentDoc);

        ODocument postMessageDoc = commentDoc.field("postMessage");
        PostMessage postMessage = PostMessageConverter.buildPostMessageWithoutChild(postMessageDoc);
        messageComment.setPostMessage(postMessage);
 
        return messageComment;
    }


}

package com.tissue.core.mapper;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.profile.User;
import com.tissue.core.plan.PostMessageComment;
import com.tissue.core.plan.PostMessage;
import com.tissue.core.plan.Post;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class PostMessageCommentMapper {

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
                User user = UserMapper.buildUser(userDoc);
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
        PostMessage postMessage = PostMessageMapper.buildPostMessageWithoutChild(postMessageDoc);
        messageComment.setPostMessage(postMessage);
 
        return messageComment;
    }


}

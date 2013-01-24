package com.tissue.core.mapper;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.social.User;
import com.tissue.core.plan.PostMessageComment;
import com.tissue.core.plan.PostMessage;
import com.tissue.core.plan.Post;

import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class PostMessageCommentMapper {

    public static ODocument convertPostMessageComment(PostMessageComment postMessageComment) {
        ODocument commentDoc = new ODocument("PostMessageComment");
        commentDoc.field("content", postMessageComment.getContent());
        return commentDoc;
    }

    public static PostMessageComment buildPostMessageComment(ODocument commentDoc) {

        PostMessageComment messageComment = new PostMessageComment();
        messageComment.setId(OrientIdentityUtil.encode(commentDoc.getIdentity().toString()));
       
        String commentContent = commentDoc.field("content", String.class);
        messageComment.setContent(commentContent);

        Set<ODocument> inEdges = commentDoc.field("in");
        for(ODocument inEdge : inEdges) {
            String label = inEdge.field("label", String.class);
            if("postMessageComment".equals(label)) {
                Date createTime = inEdge.field("createTime", Date.class);
                messageComment.setCreateTime(createTime);

                ODocument userDoc = inEdge.field("out");
                User user = UserMapper.buildUserSelf(userDoc);
                messageComment.setUser(user);
                break;
            }
        }

        return messageComment;
    }

}

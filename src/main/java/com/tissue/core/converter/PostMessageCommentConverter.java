package com.tissue.core.converter;

import com.tissue.core.util.OrientIdentityUtil;

import com.tissue.domain.profile.User;
import com.tissue.domain.plan.PostMessageComment;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class PostMessageCommentConverter {

    public static List<PostMessageComment> buildMessageComments(Set<ODocument> commentsDoc) {
        if(commentsDoc == null) {
            return null;
        }

        List<PostMessageComment> messageComments = new ArrayList();

        for(ODocument commentDoc : commentsDoc) {
            String commentContent = commentDoc.field("content", String.class);
            Date commentCreateTime = commentDoc.field("createTime", Date.class);

            ODocument commentUserDoc = commentDoc.field("user");
            User commentUser = UserConverter.buildUser(commentUserDoc);

            PostMessageComment messageComment = new PostMessageComment();
            messageComment.setContent(commentContent);
            messageComment.setCreateTime(commentCreateTime);
            messageComment.setUser(commentUser);

            messageComments.add(messageComment);
        }

        return messageComments;
    }

}

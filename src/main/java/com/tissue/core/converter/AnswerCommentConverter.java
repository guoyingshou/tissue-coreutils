package com.tissue.core.converter;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.domain.profile.User;
import com.tissue.domain.plan.AnswerComment;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class AnswerCommentConverter {

    public static List<AnswerComment> buildAnswerComments(Set<ODocument> commentsDoc) {
        if(commentsDoc == null) {
            return null;
        }

        List<AnswerComment> answerComments = new ArrayList();

        for(ODocument commentDoc : commentsDoc) {
            String commentContent = commentDoc.field("content", String.class);
            Date commentCreateTime = commentDoc.field("createTime", Date.class);

            ODocument commentUserDoc = commentDoc.field("user");
            User commentUser = UserConverter.buildUser(commentUserDoc);

            AnswerComment answerComment = new AnswerComment();
            answerComment.setContent(commentContent);
            answerComment.setCreateTime(commentCreateTime);
            answerComment.setUser(commentUser);

            answerComments.add(answerComment);
        }

        return answerComments;
    }


}

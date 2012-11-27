package com.tissue.core.converter;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.domain.profile.User;
import com.tissue.domain.plan.QuestionComment;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class QuestionCommentConverter {

    public static List<QuestionComment> buildQuestionComments(Set<ODocument> commentsDoc) {
        if(commentsDoc == null) {
            return null;
        }

        List<QuestionComment> questionComments = new ArrayList();

        for(ODocument commentDoc : commentsDoc) {
            String commentContent = commentDoc.field("content", String.class);
            Date commentCreateTime = commentDoc.field("createTime", Date.class);

            ODocument commentUserDoc = commentDoc.field("user");
            User commentUser = UserConverter.buildUser(commentUserDoc);

            QuestionComment questionComment = new QuestionComment();
            questionComment.setContent(commentContent);
            questionComment.setCreateTime(commentCreateTime);
            questionComment.setUser(commentUser);

            questionComments.add(questionComment);
        }

        return questionComments;
    }


}

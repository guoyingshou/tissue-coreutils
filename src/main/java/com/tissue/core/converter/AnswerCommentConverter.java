package com.tissue.core.converter;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.domain.profile.User;
import com.tissue.domain.plan.AnswerComment;
import com.tissue.domain.plan.Answer;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class AnswerCommentConverter {

    public static ODocument convertAnswerComment(AnswerComment comment) {
        ODocument commentDoc = new ODocument("AnswerComment");
        commentDoc.field("content", comment.getContent());
        commentDoc.field("createTime", comment.getCreateTime());
        commentDoc.field("user", new ORecordId(OrientIdentityUtil.decode(comment.getUser().getId())));
        commentDoc.field("answer", new ORecordId(OrientIdentityUtil.decode(comment.getAnswer().getId())));
        return commentDoc;
    }

    public static List<AnswerComment> buildAnswerComments(Set<ODocument> commentsDoc) {
        List<AnswerComment> answerComments = new ArrayList();

        for(ODocument commentDoc : commentsDoc) {
            AnswerComment answerComment = buildAnswerComment(commentDoc);
            answerComments.add(answerComment);
        }

        return answerComments;
    }

    public static AnswerComment buildAnswerComment(ODocument commentDoc) {
        String commentContent = commentDoc.field("content", String.class);
        Date commentCreateTime = commentDoc.field("createTime", Date.class);

        ODocument commentUserDoc = commentDoc.field("user");
        User commentUser = UserConverter.buildUser(commentUserDoc);

        AnswerComment answerComment = new AnswerComment();
        answerComment.setContent(commentContent);
        answerComment.setCreateTime(commentCreateTime);
        answerComment.setUser(commentUser);

        return answerComment;
    }

    public static AnswerComment buildAnswerCommentWithParent(ODocument commentDoc) {
        String commentContent = commentDoc.field("content", String.class);
        Date commentCreateTime = commentDoc.field("createTime", Date.class);

        ODocument commentUserDoc = commentDoc.field("user");
        User commentUser = UserConverter.buildUser(commentUserDoc);

        ODocument answerDoc = commentDoc.field("answer");
        Answer answer = AnswerConverter.buildAnswerWithoutChild(answerDoc);

        AnswerComment answerComment = new AnswerComment();
        answerComment.setContent(commentContent);
        answerComment.setCreateTime(commentCreateTime);
        answerComment.setUser(commentUser);
        answerComment.setAnswer(answer);

        return answerComment;
    }


}

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
        AnswerComment answerComment = new AnswerComment();
        answerComment.setId(OrientIdentityUtil.encode(commentDoc.getIdentity().toString()));

        String commentContent = commentDoc.field("content", String.class);
        answerComment.setContent(commentContent);

        Set<ODocument> inEdges = commentDoc.field("in");
        for(ODocument inEdge : inEdges) {
            //if(inEdge.field("target").equals("answerComment")) {
            if("EdgeAnswerComment".equals(inEdge.getClassName())) {
                Date createTime = inEdge.field("createTime", Date.class);
                answerComment.setCreateTime(createTime);

                ODocument userDoc = inEdge.field("out");
                User user = UserConverter.buildUser(userDoc);
                answerComment.setUser(user);
                break;
            }
        }

        return answerComment;
    }

    public static AnswerComment buildAnswerCommentWithParent(ODocument commentDoc) {

        AnswerComment answerComment = buildAnswerComment(commentDoc);

        ODocument answerDoc = commentDoc.field("answer");
        Answer answer = AnswerConverter.buildAnswerWithoutChild(answerDoc);
        answerComment.setAnswer(answer);

        return answerComment;
    }
}

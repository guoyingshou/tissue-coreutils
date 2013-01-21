package com.tissue.core.mapper;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.social.User;
import com.tissue.core.plan.AnswerComment;
import com.tissue.core.plan.Answer;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class AnswerCommentMapper {

    public static ODocument convertAnswerComment(AnswerComment comment) {
        ODocument commentDoc = new ODocument("AnswerComment");
        commentDoc.field("content", comment.getContent());
        commentDoc.field("answer", new ORecordId(OrientIdentityUtil.decode(comment.getAnswer().getId())));
        return commentDoc;
    }

    public static AnswerComment buildAnswerComment(ODocument commentDoc) {
        AnswerComment answerComment = new AnswerComment();
        answerComment.setId(OrientIdentityUtil.encode(commentDoc.getIdentity().toString()));

        String commentContent = commentDoc.field("content", String.class);
        answerComment.setContent(commentContent);

        Set<ODocument> inEdges = commentDoc.field("in");
        for(ODocument inEdge : inEdges) {
            String label = inEdge.field("label", String.class);
            if("answerComment".equals(label)) {
                Date createTime = inEdge.field("createTime", Date.class);
                answerComment.setCreateTime(createTime);

                ODocument userDoc = inEdge.field("out");
                User user = UserMapper.buildUserSelf(userDoc);
                answerComment.setUser(user);
                break;
            }
        }

        return answerComment;
    }

    public static AnswerComment buildAnswerCommentDetails(ODocument commentDoc) {

        AnswerComment answerComment = buildAnswerComment(commentDoc);

        ODocument answerDoc = commentDoc.field("answer");
        Answer answer = AnswerMapper.buildAnswer(answerDoc);
        answerComment.setAnswer(answer);

        return answerComment;
    }

    /**
    public static List<AnswerComment> buildAnswerComments(Set<ODocument> commentsDoc) {
        List<AnswerComment> answerComments = new ArrayList();

        for(ODocument commentDoc : commentsDoc) {
            String status = commentDoc.field("status", String.class);
            if(status == null) {
                AnswerComment answerComment = buildAnswerComment(commentDoc);
                answerComments.add(answerComment);
            }
        }

        return answerComments;
    }
    */

}

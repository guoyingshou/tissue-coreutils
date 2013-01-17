package com.tissue.core.mapper;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.social.User;
import com.tissue.core.plan.QuestionComment;
import com.tissue.core.plan.Post;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class QuestionCommentMapper {

    public static ODocument convertQuestionComment(QuestionComment comment) {
        ODocument commentDoc = new ODocument("QuestionComment");
        commentDoc.field("content", comment.getContent());
        commentDoc.field("question", new ORecordId(OrientIdentityUtil.decode(comment.getQuestion().getId())));
        return commentDoc;
    }

    public static List<QuestionComment> buildQuestionComments(Set<ODocument> commentsDoc) {
        List<QuestionComment> questionComments = new ArrayList();

        for(ODocument commentDoc : commentsDoc) {
            String status = commentDoc.field("status", String.class);
            if(status == null) {
                QuestionComment questionComment = buildQuestionComment(commentDoc);
                questionComments.add(questionComment);
            }
        }

        return questionComments;
    }

    public static QuestionComment buildQuestionComment(ODocument commentDoc) {

        QuestionComment questionComment = new QuestionComment();
        questionComment.setId(OrientIdentityUtil.encode(commentDoc.getIdentity().toString()));

        String commentContent = commentDoc.field("content", String.class);
        questionComment.setContent(commentContent);

        Set<ODocument> inEdges = commentDoc.field("in");
        for(ODocument inEdge : inEdges) {
            if("EdgeQuestionComment".equals(inEdge.getClassName())) {
                Date createTime = inEdge.field("createTime", Date.class);
                questionComment.setCreateTime(createTime);

                ODocument userDoc = inEdge.field("out");
                User user = UserMapper.buildUser(userDoc);
                questionComment.setUser(user);
                break;
            }
        }
        return questionComment;
    }

    public static QuestionComment buildQuestionCommentDetails(ODocument commentDoc) {

        QuestionComment questionComment = buildQuestionComment(commentDoc);

        ODocument questionDoc = commentDoc.field("question");
        Post question = PostMapper.buildPost(questionDoc);
        questionComment.setQuestion(question);

        return questionComment;
    }

}

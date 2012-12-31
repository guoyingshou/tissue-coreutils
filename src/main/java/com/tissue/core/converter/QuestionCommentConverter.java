package com.tissue.core.converter;

import com.tissue.core.util.OrientIdentityUtil;

import com.tissue.domain.profile.User;
import com.tissue.domain.plan.QuestionComment;
import com.tissue.domain.plan.Post;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class QuestionCommentConverter {

    public static ODocument convertQuestionComment(QuestionComment comment) {
        ODocument commentDoc = new ODocument("QuestionComment");
        commentDoc.field("content", comment.getContent());
        commentDoc.field("createTime", comment.getCreateTime());
        commentDoc.field("user", new ORecordId(OrientIdentityUtil.decode(comment.getUser().getId())));
        commentDoc.field("question", new ORecordId(OrientIdentityUtil.decode(comment.getQuestion().getId())));

        return commentDoc;
    }

    public static List<QuestionComment> buildQuestionComments(Set<ODocument> commentsDoc) {
        List<QuestionComment> questionComments = new ArrayList();

        for(ODocument commentDoc : commentsDoc) {
            QuestionComment questionComment = buildQuestionComment(commentDoc);
            questionComments.add(questionComment);
        }

        return questionComments;
    }

    public static QuestionComment buildQuestionComment(ODocument commentDoc) {
        String commentContent = commentDoc.field("content", String.class);
        Date commentCreateTime = commentDoc.field("createTime", Date.class);

        ODocument commentUserDoc = commentDoc.field("user");
        User commentUser = UserConverter.buildUser(commentUserDoc);

        QuestionComment questionComment = new QuestionComment();
        questionComment.setId(OrientIdentityUtil.encode(commentDoc.getIdentity().toString()));
        questionComment.setContent(commentContent);
        questionComment.setCreateTime(commentCreateTime);
        questionComment.setUser(commentUser);

        return questionComment;
 
    }

    public static QuestionComment buildQuestionCommentWithParent(ODocument commentDoc) {
        String commentContent = commentDoc.field("content", String.class);
        Date commentCreateTime = commentDoc.field("createTime", Date.class);

        ODocument commentUserDoc = commentDoc.field("user");
        User commentUser = UserConverter.buildUser(commentUserDoc);

        ODocument questionDoc = commentDoc.field("question");
        Post question = PostConverter.buildPostWithoutChild(questionDoc);

        QuestionComment questionComment = new QuestionComment();
        questionComment.setId(OrientIdentityUtil.encode(commentDoc.getIdentity().toString()));
        questionComment.setContent(commentContent);
        questionComment.setCreateTime(commentCreateTime);
        questionComment.setUser(commentUser);
        questionComment.setQuestion(question);

        return questionComment;
 
    }

}

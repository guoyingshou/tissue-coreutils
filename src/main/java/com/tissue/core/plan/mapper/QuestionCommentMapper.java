package com.tissue.core.plan.mapper;

import com.tissue.core.Account;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.core.plan.command.QuestionCommentCommand;
import com.tissue.core.plan.QuestionComment;
import com.tissue.core.plan.Post;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class QuestionCommentMapper {

    public static ODocument convertQuestionComment(QuestionCommentCommand comment) {
        ODocument commentDoc = new ODocument("QuestionComment");
        commentDoc.field("content", comment.getContent());
        commentDoc.field("question", new ORecordId(comment.getQuestion().getId()));
        return commentDoc;
    }

    public static QuestionComment buildQuestionComment(ODocument commentDoc) {

        QuestionComment questionComment = new QuestionComment();
        questionComment.setId(commentDoc.getIdentity().toString());

        String commentContent = commentDoc.field("content", String.class);
        questionComment.setContent(commentContent);

        Set<ODocument> inEdges = commentDoc.field("in");
        for(ODocument inEdge : inEdges) {
            String label = inEdge.field("label", String.class);
            if("questionComment".equals(label)) {
                Date createTime = inEdge.field("createTime", Date.class);
                questionComment.setCreateTime(createTime);

                ODocument accountDoc = inEdge.field("out");
                Account account = AccountMapper.buildAccount(accountDoc);
                questionComment.setAccount(account);
                break;
            }
        }
        return questionComment;
    }
}

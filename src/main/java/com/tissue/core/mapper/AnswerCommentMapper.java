package com.tissue.core.mapper;

import com.tissue.core.Account;
import com.tissue.core.command.AnswerCommentCommand;
import com.tissue.core.plan.AnswerComment;
import com.tissue.core.plan.Answer;
import com.tissue.core.command.AnswerCommentCommand;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class AnswerCommentMapper {

    public static ODocument convertAnswerComment(AnswerCommentCommand comment) {
        ODocument commentDoc = new ODocument("AnswerComment");
        commentDoc.field("content", comment.getContent());
        commentDoc.field("answer", new ORecordId(comment.getAnswer().getId()));
        return commentDoc;
    }

    public static AnswerComment buildAnswerComment(ODocument commentDoc) {
        AnswerComment answerComment = new AnswerComment();
        answerComment.setId(commentDoc.getIdentity().toString());

        String commentContent = commentDoc.field("content", String.class);
        answerComment.setContent(commentContent);

        Set<ODocument> inEdges = commentDoc.field("in");
        for(ODocument inEdge : inEdges) {
            String label = inEdge.field("label", String.class);
            if("answerComment".equals(label)) {
                Date createTime = inEdge.field("createTime", Date.class);
                answerComment.setCreateTime(createTime);

                ODocument accountDoc = inEdge.field("out");
                Account account = AccountMapper.buildAccount(accountDoc);
                answerComment.setAccount(account);
                break;
            }
        }

        return answerComment;
    }

}

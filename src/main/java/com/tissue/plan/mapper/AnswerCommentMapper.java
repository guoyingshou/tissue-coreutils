package com.tissue.plan.mapper;

import com.tissue.core.Account;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.plan.command.AnswerCommentCommand;
import com.tissue.plan.AnswerComment;
import com.tissue.plan.Answer;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class AnswerCommentMapper {

    public static ODocument convertAnswerComment(AnswerCommentCommand command) {
        ODocument doc = new ODocument("AnswerComment");
        doc.field("content", command.getContent());
        doc.field("answer", new ORecordId(command.getAnswer().getId()));
        return doc;
    }

    public static AnswerComment buildAnswerComment(ODocument doc) {
        AnswerComment answerComment = new AnswerComment();
        answerComment.setId(doc.getIdentity().toString());

        String commentContent = doc.field("content", String.class);
        answerComment.setContent(commentContent);

        /**
        ODocument edgeCreatePostDoc = doc.field("in_");

        String category = edgeCreatePostDoc.field("category", String.class);
        answerComment.setType(category);

        ODocument accountDoc = edgeCreatePostDoc.field("out");
        Account account = AccountMapper.buildAccount(accountDoc);
        answerComment.setAccount(account);
        */
 
        return answerComment;
    }

}

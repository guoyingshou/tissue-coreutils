package com.tissue.plan.mapper;

import com.tissue.core.Account;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.plan.command.QuestionCommentCommand;
import com.tissue.plan.QuestionComment;
import com.tissue.plan.Post;

//import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class QuestionCommentMapper {

    public static ODocument convertQuestionComment(QuestionCommentCommand comment) {
        ODocument commentDoc = new ODocument("QuestionComment");
        commentDoc.field("content", comment.getContent());
        //commentDoc.field("question", new ORecordId(comment.getQuestion().getId()));
        return commentDoc;
    }

    public static QuestionComment buildQuestionComment(ODocument doc) {

        QuestionComment questionComment = new QuestionComment();
        questionComment.setId(doc.getIdentity().toString());

        String commentContent = doc.field("content", String.class);
        questionComment.setContent(commentContent);

        /**
        ODocument accountDoc = doc.field("out_PostsAccount.in");
        Account account = AccountMapper.buildAccount(accountDoc);
        questionComment.setAccount(account);
        */

        AccountMapper.setAccount(questionComment, doc);

        return questionComment;
    }
}

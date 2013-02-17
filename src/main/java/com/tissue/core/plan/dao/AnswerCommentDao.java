package com.tissue.core.plan.dao;

import com.tissue.core.command.AnswerCommentCommand;
//import com.tissue.core.plan.Answer;
//import com.tissue.core.plan.AnswerComment;

public interface AnswerCommentDao {

    /**
     * Add a comment to the specific post message.
     */
    //String create(AnswerComment comment);
    String create(AnswerCommentCommand command);

    //void update(AnswerComment comment);
    void update(AnswerCommentCommand command);

    void delete(String commentId);
}

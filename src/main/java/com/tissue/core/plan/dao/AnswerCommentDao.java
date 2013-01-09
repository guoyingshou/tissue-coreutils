package com.tissue.core.plan.dao;

import com.tissue.core.plan.Answer;
import com.tissue.core.plan.AnswerComment;

public interface AnswerCommentDao {

    /**
     * Add a comment to the specific post message.
     */
    AnswerComment create(AnswerComment comment);

    void update(AnswerComment comment);

    void delete(String commentId);
}

package com.tissue.core.plan.dao;

import com.tissue.core.plan.QuestionComment;
import java.util.List;

public interface QuestionCommentDao {

    /**
     * Add a comment to the specific question.
     */
    QuestionComment create(QuestionComment comment);

    void update(QuestionComment comment);

    void delete(String commentId);
}

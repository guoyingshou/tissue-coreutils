package com.tissue.core.plan.dao;

//import com.tissue.core.plan.QuestionComment;
import com.tissue.core.command.QuestionCommentCommand;
import java.util.List;

public interface QuestionCommentDao {

    /**
     * Add a comment to the specific question.
    String create(QuestionComment comment);
    void update(QuestionComment comment);
     */
    String create(QuestionCommentCommand command);

    void update(QuestionCommentCommand command);
 
    void delete(String commentId);
}

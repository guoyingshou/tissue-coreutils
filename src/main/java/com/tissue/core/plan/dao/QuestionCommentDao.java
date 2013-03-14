package com.tissue.core.plan.dao;

import com.tissue.core.plan.QuestionComment;
import com.tissue.core.command.QuestionCommentCommand;
import java.util.List;

public interface QuestionCommentDao {

    String create(QuestionCommentCommand command);

    void update(QuestionCommentCommand command);
 
}

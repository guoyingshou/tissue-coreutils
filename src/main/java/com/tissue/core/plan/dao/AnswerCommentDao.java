package com.tissue.core.plan.dao;

import com.tissue.core.command.AnswerCommentCommand;
import com.tissue.core.plan.AnswerComment;

public interface AnswerCommentDao {

    String create(AnswerCommentCommand command);

    void update(AnswerCommentCommand command);

}

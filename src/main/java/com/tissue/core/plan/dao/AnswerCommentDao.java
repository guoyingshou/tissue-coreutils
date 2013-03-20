package com.tissue.core.plan.dao;

import com.tissue.core.dao.ContentDao;
import com.tissue.core.plan.command.AnswerCommentCommand;
import com.tissue.core.plan.AnswerComment;

public interface AnswerCommentDao extends ContentDao {

    String create(AnswerCommentCommand command);

    AnswerComment getAnswerComment(String answerCommentId);

}

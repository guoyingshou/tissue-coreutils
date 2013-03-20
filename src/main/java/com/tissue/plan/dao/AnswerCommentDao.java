package com.tissue.plan.dao;

import com.tissue.core.dao.ContentDao;
import com.tissue.plan.command.AnswerCommentCommand;
import com.tissue.plan.AnswerComment;

public interface AnswerCommentDao extends ContentDao {

    String create(AnswerCommentCommand command);

    AnswerComment getAnswerComment(String answerCommentId);

}

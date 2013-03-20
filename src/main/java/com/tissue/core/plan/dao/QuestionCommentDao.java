package com.tissue.core.plan.dao;

import com.tissue.core.dao.ContentDao;
import com.tissue.core.plan.command.QuestionCommentCommand;
import com.tissue.core.plan.QuestionComment;
import java.util.List;

public interface QuestionCommentDao {

    String create(QuestionCommentCommand command);

    QuestionComment getQuestionComment(String questionCommentId);

}

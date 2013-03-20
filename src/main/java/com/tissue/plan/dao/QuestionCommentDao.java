package com.tissue.plan.dao;

import com.tissue.core.dao.ContentDao;
import com.tissue.plan.command.QuestionCommentCommand;
import com.tissue.plan.QuestionComment;
import java.util.List;

public interface QuestionCommentDao {

    String create(QuestionCommentCommand command);

    QuestionComment getQuestionComment(String questionCommentId);

}

package com.tissue.core.plan.dao;

import com.tissue.core.dao.ContentDao;
import com.tissue.core.plan.command.AnswerCommand;
import com.tissue.core.plan.Answer;
import java.util.List;

public interface AnswerDao extends ContentDao {

    String create(AnswerCommand command);

    Answer getAnswer(String answerId);


}

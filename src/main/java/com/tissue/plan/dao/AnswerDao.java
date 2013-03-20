package com.tissue.plan.dao;

import com.tissue.core.dao.ContentDao;
import com.tissue.plan.command.AnswerCommand;
import com.tissue.plan.Answer;
import java.util.List;

public interface AnswerDao extends ContentDao {

    String create(AnswerCommand command);

    Answer getAnswer(String answerId);


}

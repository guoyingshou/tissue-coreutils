package com.tissue.core.plan.dao;

import com.tissue.core.plan.Answer;
import com.tissue.core.command.AnswerCommand;
import java.util.List;

public interface AnswerDao {

    String create(AnswerCommand command);

    Answer getAnswer(String answerId);

    //void update(AnswerCommand command);


}

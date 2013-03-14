package com.tissue.core.plan.dao;

import com.tissue.core.command.QuestionCommand;
import com.tissue.core.plan.Question;
import com.tissue.core.plan.Topic;
import java.util.List;

public interface QuestionDao {

    String create(QuestionCommand command);

    void update(QuestionCommand command);

    Question getQuestion(String id);

    Topic getTopic(String questionId);

}

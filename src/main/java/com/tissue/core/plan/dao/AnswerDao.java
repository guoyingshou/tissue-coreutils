package com.tissue.core.plan.dao;

import com.tissue.core.plan.Answer;
import com.tissue.core.command.AnswerCommand;
import java.util.List;

public interface AnswerDao {

    /**
     * Add an answer to the specific question.
     */

    Answer create(AnswerCommand command);

    void update(AnswerCommand command);

    void delete(String answerId);

}

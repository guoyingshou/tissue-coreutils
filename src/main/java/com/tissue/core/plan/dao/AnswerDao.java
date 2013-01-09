package com.tissue.core.plan.dao;

import com.tissue.core.plan.Answer;
import java.util.List;

public interface AnswerDao {

    /**
     * Add an answer to the specific question.
     */
    Answer create(Answer answer);

    void update(Answer answer);

    void delete(String answerId);

}

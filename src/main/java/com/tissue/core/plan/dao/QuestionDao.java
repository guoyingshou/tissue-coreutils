package com.tissue.core.plan.dao;

import com.tissue.core.command.QuestionCommand;
import com.tissue.core.plan.Question;
import java.util.List;

public interface QuestionDao extends PostDao {

    String create(QuestionCommand command);

    void update(QuestionCommand command);

    Question getQuestion(String id);

    long getQuestionsCountByTopic(String topicId);

    List<Question> getPagedQuestionsByTopic(String topicId, int page, int size);

}

package com.tissue.core.plan.dao;

import com.tissue.core.plan.Question;
import java.util.List;

public interface QuestionDao extends PostDao {

    Question getQuestion(String id);

    long getQuestionsCountByTopic(String topicId);

    List<Question> getPagedQuestionsByTopic(String topicId, int page, int size);

}

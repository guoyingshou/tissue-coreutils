package com.tissue.core.plan.dao;

import com.tissue.core.command.PostCommand;
import com.tissue.core.plan.Question;
import com.tissue.core.plan.Topic;
import java.util.List;

public interface QuestionDao {

    String create(PostCommand command);

    //void update(QuestionCommand command);

    Question getQuestion(String id);

    //List<Question> getLatestQuestions(int limit);

    /**
     * topic
    Topic getTopic(String questionId);

    String getTopicId(String questionId);
     */
    
}

package com.tissue.plan.dao;

import com.tissue.plan.Question;
import java.util.List;

public interface QuestionDao extends PostDao {

    Question getQuestion(String id);

}

package com.tissue.plan;

import com.tissue.core.UserGeneratedContent;

public class QuestionComment extends UserGeneratedContent {

    private Question question;
    public void setQuestion(Question question) {
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }
}

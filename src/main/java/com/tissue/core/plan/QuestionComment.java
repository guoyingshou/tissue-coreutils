package com.tissue.core.plan;

public class QuestionComment extends Comment {

    private Question question;
    public void setQuestion(Question question) {
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }
}

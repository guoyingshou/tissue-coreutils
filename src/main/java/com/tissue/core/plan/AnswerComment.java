package com.tissue.core.plan;

public class AnswerComment extends Comment {

    private Answer answer;

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Answer getAnswer() {
        return answer;
    }
}

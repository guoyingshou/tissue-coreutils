package com.tissue.core.plan;

import com.tissue.core.UserGeneratedContent;

public class AnswerComment extends UserGeneratedContent {

    private Answer answer;

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Answer getAnswer() {
        return answer;
    }
}

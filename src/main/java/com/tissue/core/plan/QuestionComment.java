package com.tissue.core.plan;

public class QuestionComment extends ContentParent {

    private Post question;

    public void setQuestion(Post question) {
        this.question = question;
    }

    public Post getQuestion() {
        return question;
    }
}

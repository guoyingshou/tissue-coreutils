package com.tissue.core.plan;

import java.util.List;

public class Question extends PostWrapper {

    private List<QuestionComment> comments;
    private List<Answer> answers;

    public Question(Post post) {
        super(post);
    }

    public void setComments(List<QuestionComment> comments) {
        this.comments = comments;
    }

    public List<QuestionComment> getComments() {
        return comments;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

}

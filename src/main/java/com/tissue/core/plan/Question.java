package com.tissue.core.plan;

import java.util.List;
import java.util.ArrayList;

public class Question extends PostWrapper {

    private List<QuestionComment> comments;
    private List<Answer> answers;

    public Question(Post post) {
        super(post);
    }

    public void addComment(QuestionComment comment) {
        if(comments == null) {
            comments = new ArrayList();
        }
        comments.add(comment);
    }

    public void setComments(List<QuestionComment> comments) {
        this.comments = comments;
    }

    public List<QuestionComment> getComments() {
        return comments;
    }

    public void addAnswer(Answer answer) {
        if(answers == null) {
            answers = new ArrayList();
        }
        answers.add(answer);
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

}

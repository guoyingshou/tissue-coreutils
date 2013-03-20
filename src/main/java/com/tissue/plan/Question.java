package com.tissue.plan;

import java.util.List;
import java.util.ArrayList;

public class Question extends Post {

    private List<QuestionComment> comments;
    private List<Answer> answers;

    public void addComment(QuestionComment comment) {
        if(comments == null) {
            comments = new ArrayList<QuestionComment>();
        }
        comments.add(comment);
    }

    /**
    public void setComments(List<QuestionComment> comments) {
        this.comments = comments;
    }
    */

    public List<QuestionComment> getComments() {
        return comments;
    }

    public void addAnswer(Answer answer) {
        if(answers == null) {
            answers = new ArrayList();
        }
        answers.add(answer);
    }

    /**
    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
    */

    public List<Answer> getAnswers() {
        return answers;
    }

}

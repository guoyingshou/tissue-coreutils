package com.tissue.core.plan;

import com.tissue.core.UserGeneratedContent;

import java.util.List;
import java.util.ArrayList;

public class Answer extends UserGeneratedContent {

    private Question question;
    List<AnswerComment> comments;

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }

    public void addComment(AnswerComment comment) {
        if(comments == null) {
            comments = new ArrayList();
        }
        comments.add(comment);
    }

    public void setComments(List<AnswerComment> comments) {
        this.comments = comments;
    }

    public List<AnswerComment> getComments() {
        return comments;
    }
}

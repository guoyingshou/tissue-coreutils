package com.tissue.core.plan;

import java.util.List;
import java.util.ArrayList;

public class Answer extends ContentParent {

    private Post question;

    List<AnswerComment> comments;

    public void setQuestion(Post question) {
        this.question = question;
    }

    public Post getQuestion() {
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

package com.tissue.core.plan;

import com.tissue.core.UserGeneratedContent;

import java.util.List;
import java.util.ArrayList;

public class Message extends UserGeneratedContent {

    private Article article;
    private List<MessageReply> replies;

    public void setArticle(Article article) {
        this.article = article;
    }

    public Article getArticle() {
        return article;
    }

    public void addReply(MessageReply reply) {
        if(replies == null) {
            replies = new ArrayList();
        }
        replies.add(reply);
    }

    /**
    public void setReplies(List<MessageReply> replies) {
        this.replies = replies;
    }
    */

    public List<MessageReply> getReplies() {
        return replies;
    }

}

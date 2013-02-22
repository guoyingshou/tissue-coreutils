package com.tissue.core.mapper;

import com.tissue.core.command.PostMessageCommentCommand;
import com.tissue.core.social.Account;
import com.tissue.core.plan.PostMessageComment;
import com.tissue.core.plan.PostMessage;
import com.tissue.core.plan.Post;

import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class PostMessageCommentMapper {

    public static ODocument convertPostMessageComment(PostMessageCommentCommand postMessageComment) {
        ODocument commentDoc = new ODocument("PostMessageComment");
        commentDoc.field("content", postMessageComment.getContent());
        return commentDoc;
    }

    public static PostMessageComment buildPostMessageComment(ODocument commentDoc) {

        PostMessageComment messageComment = new PostMessageComment();
        messageComment.setId(commentDoc.getIdentity().toString());
       
        String commentContent = commentDoc.field("content", String.class);
        messageComment.setContent(commentContent);

        Set<ODocument> inEdges = commentDoc.field("in");
        for(ODocument inEdge : inEdges) {
            String label = inEdge.field("label", String.class);
            if("postMessageComment".equals(label)) {
                Date createTime = inEdge.field("createTime", Date.class);
                messageComment.setCreateTime(createTime);

                ODocument accountDoc = inEdge.field("out");
                Account account = AccountMapper.buildAccount(accountDoc);
                messageComment.setAccount(account);
                break;
            }
        }

        return messageComment;
    }

}

package com.tissue.core.mapper;

import com.tissue.core.command.PostCommand;
import com.tissue.core.social.User;
import com.tissue.core.plan.Post;
import com.tissue.core.plan.Cnt;
import com.tissue.core.plan.PostMessage;
import com.tissue.core.plan.Question;
import com.tissue.core.plan.QuestionComment;
import com.tissue.core.plan.Answer;
import com.tissue.core.plan.Plan;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.record.OTrackedList;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class PostMapper {

    public static ODocument convert(PostCommand postCommand) {
        ODocument doc = new ODocument(postCommand.getType());
        doc.field("title", postCommand.getTitle());
        doc.field("content", postCommand.getContent());
        doc.field("type", postCommand.getType());
        doc.field("createTime", new Date());
        return doc;
    }

    public static Post buildPostSelf(ODocument doc) {
        Post post = new Post();
        post.setId(doc.getIdentity().toString());

        String postTitle = doc.field("title", String.class);
        post.setTitle(postTitle);

        String postContent = doc.field("content", String.class);
        post.setContent(postContent);

        String postType = doc.field("type", String.class);
        post.setType(postType);

        Date createTime = doc.field("createTime", Date.class);
        post.setCreateTime(createTime);

        Boolean deleted = doc.field("deleted", Boolean.class);
        if(deleted != null) {
            post.setDeleted(deleted);
        }
 
        return post;
    }

    public static Post buildPost(ODocument postDoc) {
        Post post = buildPostSelf(postDoc);

        Set<ODocument> inEdgesDoc = postDoc.field("in");
        for(ODocument inEdgeDoc : inEdgesDoc) {
            String label = inEdgeDoc.field("label", String.class);
            if("concept".equals(label) || "note".equals(label) || "tutorial".equals(label) || "question".equals(label)) {
                ODocument userDoc = inEdgeDoc.field("out");
                User user = UserMapper.buildUserSelf(userDoc);
                post.setUser(user);
                break;
            }
        }
        return post;
    }

    public static Post buildPostDetails(ODocument postDoc) {
        Post post = buildPost(postDoc);

        ODocument planDoc = postDoc.field("plan");
        Plan plan = PlanMapper.buildPlanDetails(planDoc);
        post.setPlan(plan);

        if("question".equals(post.getType())) {
            Question q = new Question(post);
            List<ODocument> questionCommentsDoc = postDoc.field("comments");
            if(questionCommentsDoc != null) {
                for(ODocument commentDoc : questionCommentsDoc) {
                    String status = commentDoc.field("status", String.class);
                    if(status == null) {
                        QuestionComment comment = QuestionCommentMapper.buildQuestionComment(commentDoc);
                        q.addComment(comment);
                    }
                }
            }
            List<ODocument> answersDoc = postDoc.field("answers");
            if(answersDoc != null) {
                for(ODocument answerDoc : answersDoc) {
                    String status = answerDoc.field("status", String.class);
                    if(status == null) {
                        Answer answer = AnswerMapper.buildAnswerDetails(answerDoc);
                        q.addAnswer(answer);
                    }
                }
            }
            return q;
        }
        else {
            Cnt cnt = new Cnt(post);
            List<ODocument> messagesDoc = postDoc.field("messages");
            if(messagesDoc != null) {
                for(ODocument messageDoc : messagesDoc) {
                    String status = messageDoc.field("status", String.class);
                    if(status == null) {
                        PostMessage postMessage = PostMessageMapper.buildPostMessageDetails(messageDoc);
                        cnt.addPostMessage(postMessage);
                    }
                }
            }
            return cnt;
        }
    }

}

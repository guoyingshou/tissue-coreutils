package com.tissue.core.mapper;

import com.tissue.core.util.OrientIdentityUtil;
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

    public static ODocument convert(Post post) {
        ODocument doc = new ODocument(post.getType());
        doc.field("title", post.getTitle());
        doc.field("content", post.getContent());
        doc.field("type", post.getType());
        doc.field("createTime", post.getCreateTime());
        return doc;
    }

    public static Post buildPostSelf(ODocument postDoc) {
        Post post = new Post();
        post.setId(OrientIdentityUtil.encode(postDoc.getIdentity().toString()));

        String postTitle = postDoc.field("title", String.class);
        post.setTitle(postTitle);

        String postContent = postDoc.field("content", String.class);
        post.setContent(postContent);

        String postType = postDoc.field("type", String.class);
        post.setType(postType);

        Date createTime = postDoc.field("createTime", Date.class);
        post.setCreateTime(createTime);
 
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
        Post post = buildPostSelf(postDoc);

        ODocument planDoc = postDoc.field("plan");
        Plan plan = PlanMapper.buildPlan(planDoc);
        post.setPlan(plan);

        if("question".equals(post.getType())) {
            Question q = new Question(post);
            Set<ODocument> questionCommentsDoc = postDoc.field("comments");
            if(questionCommentsDoc != null) {
                for(ODocument commentDoc : questionCommentsDoc) {
                    String status = commentDoc.field("status", String.class);
                    if(status == null) {
                        QuestionComment comment = QuestionCommentMapper.buildQuestionComment(commentDoc);
                        q.addComment(comment);
                    }
                }
                //List<QuestionComment> questionComments = QuestionCommentMapper.buildQuestionComments(questionCommentsDoc);
                //q.setComments(questionComments);
            }

            Set<ODocument> answersDoc = postDoc.field("answers");
            if(answersDoc != null) {
                for(ODocument answerDoc : answersDoc) {
                    String status = answerDoc.field("status", String.class);
                    if(status == null) {
                        Answer answer = AnswerMapper.buildAnswer(answerDoc);
                        q.addAnswer(answer);
                    }
                }

                //List<Answer> answers = AnswerMapper.buildAnswers(answersDoc);
                //q.setAnswers(answers);
            }

            return q;
        }
        else {
            Cnt cnt = new Cnt(post);
            Set<ODocument> messagesDoc = postDoc.field("messages");
            if(messagesDoc != null) {
                for(ODocument messageDoc : messagesDoc) {
                    String status = messageDoc.field("status", String.class);
                    if(status == null) {
                        PostMessage postMessage = PostMessageMapper.buildPostMessage(messageDoc);
                        cnt.addPostMessage(postMessage);
                    }
                //List<PostMessage> messages = PostMessageMapper.buildPostMessages(messagesDoc);
                //cnt.setMessages(messages);
                }
            }
            return cnt;
        }
    }

}

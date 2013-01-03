package com.tissue.core.converter;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.domain.profile.User;
import com.tissue.domain.plan.Post;
import com.tissue.domain.plan.PostMessage;
import com.tissue.domain.plan.Answer;
import com.tissue.domain.plan.QuestionComment;
import com.tissue.domain.plan.Plan;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class PostConverter {

    public static ODocument convert(Post post) {
        ODocument doc = new ODocument("Post");
        doc.field("title", post.getTitle());
        doc.field("content", post.getContent());
        doc.field("createTime", post.getCreateTime());
        doc.field("type", post.getType());

        doc.field("plan", new ORecordId(OrientIdentityUtil.decode(post.getPlan().getId())));
        doc.field("user", new ORecordId(OrientIdentityUtil.decode(post.getUser().getId())));

        return doc;
    }

    public static Post buildPost(ODocument postDoc) {
        Post post = new Post();
        post.setId(OrientIdentityUtil.encode(postDoc.getIdentity().toString()));

        String title = postDoc.field("title", String.class);
        post.setTitle(title);

        String type = postDoc.field("type", String.class);
        post.setType(type);

        String content = postDoc.field("content", String.class);
        post.setContent(content);

        Date createTime = postDoc.field("createTime", Date.class);
        post.setCreateTime(createTime);

        ODocument userDoc = postDoc.field("user");
        User postUser = UserConverter.buildUser(userDoc);
        post.setUser(postUser);

        ODocument planDoc = postDoc.field("plan");
        if(planDoc != null) {
            Plan postPlan = PlanConverter.buildPlan(planDoc);
            post.setPlan(postPlan);
        }

        if("question".equals(type)) {
            Set<ODocument> questionCommentsDoc = postDoc.field("comments");
            if(questionCommentsDoc != null) {
                List<QuestionComment> questionComments = QuestionCommentConverter.buildQuestionComments(questionCommentsDoc);
                post.setComments(questionComments);
            }

            Set<ODocument> answersDoc = postDoc.field("answers");
            if(answersDoc != null) {
                List<Answer> answers = AnswerConverter.buildAnswers(answersDoc);
                post.setAnswers(answers);
            }
        }
        else {
            Set<ODocument> messagesDoc = postDoc.field("messages");
            if(messagesDoc != null) {
                List<PostMessage> messages = PostMessageConverter.buildPostMessages(messagesDoc);
                post.setMessages(messages);
            }
        }

        return post;
    }

    public static Post buildPostWithoutChild(ODocument postDoc) {

        String postTitle = postDoc.field("title", String.class);
        String postType = postDoc.field("type", String.class);
        String postContent = postDoc.field("content", String.class);
        Date postCreateTime = postDoc.field("createTime", Date.class);

        Post post = new Post();
        post.setId(OrientIdentityUtil.encode(postDoc.getIdentity().toString()));
        post.setTitle(postTitle);
        post.setContent(postContent);
        post.setType(postType);
        post.setCreateTime(postCreateTime);
 
        ODocument postUserDoc = postDoc.field("user");
        User postUser = UserConverter.buildUser(postUserDoc);
        post.setUser(postUser);

        ODocument planDoc = postDoc.field("plan");
        if(planDoc != null) {
            Plan plan = PlanConverter.buildPlan(planDoc);
            post.setPlan(plan);
        }

        return post;
    }

    public static List<Post> buildPosts(List<ODocument> postsDoc) {
        List<Post> posts = new ArrayList();
        for(ODocument postDoc : postsDoc) {
            Post post = buildPostWithoutChild(postDoc);
            posts.add(post);
        }
        return posts;
    }
}

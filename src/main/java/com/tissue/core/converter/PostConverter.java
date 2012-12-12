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
        ODocument userDoc = postDoc.field("user");
        User postUser = UserConverter.buildUser(userDoc);

        ODocument planDoc = postDoc.field("plan");
        Plan postPlan = PlanConverter.buildPlan(planDoc);

        List<PostMessage> messages = null;
        Set<ODocument> messagesDoc = postDoc.field("messages");
        if(messagesDoc != null) {
            messages = PostMessageConverter.buildPostMessages(messagesDoc);
        }

        //for question type post
        List<Answer> answers = null;
        Set<ODocument> answersDoc = postDoc.field("answers");
        if(answersDoc != null) {
            answers = AnswerConverter.buildAnswers(answersDoc);
        }

        List<QuestionComment> questionComments = null;
        Set<ODocument> questionCommentsDoc = postDoc.field("comments");
        if(questionCommentsDoc != null) {
            questionComments = QuestionCommentConverter.buildQuestionComments(questionCommentsDoc);
        }

        //construct the post after preprocessing all the necessary fields
        String title = postDoc.field("title", String.class);
        String content = postDoc.field("content", String.class);
        String type = postDoc.field("type", String.class);
        Date createTime = postDoc.field("createTime", Date.class);

        Post post = new Post();
        post.setId(OrientIdentityUtil.encode(postDoc.getIdentity().toString()));
        post.setTitle(title);
        post.setType(type);
        post.setContent(content);
        post.setCreateTime(createTime);
        post.setPlan(postPlan);
        post.setUser(postUser);

        //non question type post
        if(messages != null) {
            post.setMessages(messages);
        }

        //qustion type post
        if(questionComments != null) {
            post.setComments(questionComments);
        }
        if(answers != null) {
            post.setAnswers(answers);
        }

        return post;
    }

    public static Post buildPostWithoutChild(ODocument postDoc) {
        String postTitle = postDoc.field("title", String.class);
        String postType = postDoc.field("type", String.class);
        String postContent = postDoc.field("content", String.class);
        Date postCreateTime = postDoc.field("createTime", Date.class);

        ODocument postUserDoc = postDoc.field("user");
        User postUser = UserConverter.buildUser(postUserDoc);

        ODocument planDoc = postDoc.field("plan");
        Plan plan = PlanConverter.buildPlan(planDoc);

        Post post = new Post();
        post.setId(OrientIdentityUtil.encode(postDoc.getIdentity().toString()));
        post.setTitle(postTitle);
        post.setContent(postContent);
        post.setType(postType);
        post.setCreateTime(postCreateTime);
        post.setUser(postUser);
        post.setPlan(plan);

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

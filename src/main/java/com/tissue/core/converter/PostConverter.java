package com.tissue.core.converter;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.domain.profile.User;
import com.tissue.domain.plan.Post;
import com.tissue.domain.plan.Cnt;
import com.tissue.domain.plan.PostMessage;
import com.tissue.domain.plan.Question;
import com.tissue.domain.plan.QuestionComment;
import com.tissue.domain.plan.Answer;
import com.tissue.domain.plan.Plan;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class PostConverter {

    public static ODocument convert(Post post) {

        ODocument doc = new ODocument(post.getType());
        doc.field("title", post.getTitle());
        doc.field("content", post.getContent());
        doc.field("type", post.getType());

        doc.field("plan", new ORecordId(OrientIdentityUtil.decode(post.getPlan().getId())));

        return doc;
    }

    public static Post buildPost(ODocument postDoc) {
        Post post = buildPostWithoutChild(postDoc);

        if("question".equals(post.getType())) {
            Question q = new Question(post);
            Set<ODocument> questionCommentsDoc = postDoc.field("comments");
            if(questionCommentsDoc != null) {
                List<QuestionComment> questionComments = QuestionCommentConverter.buildQuestionComments(questionCommentsDoc);
                q.setComments(questionComments);
            }

            Set<ODocument> answersDoc = postDoc.field("answers");
            if(answersDoc != null) {
                List<Answer> answers = AnswerConverter.buildAnswers(answersDoc);
                q.setAnswers(answers);
            }

            return q;
        }
        else {
            Cnt cnt = new Cnt(post);
            Set<ODocument> messagesDoc = postDoc.field("messages");
            if(messagesDoc != null) {
                List<PostMessage> messages = PostMessageConverter.buildPostMessages(messagesDoc);
                cnt.setMessages(messages);
            }
            return cnt;
        }

    }

    public static Post buildPostWithoutChild(ODocument postDoc) {
        Post post = new Post();
        post.setId(OrientIdentityUtil.encode(postDoc.getIdentity().toString()));

        String postTitle = postDoc.field("title", String.class);
        post.setTitle(postTitle);

        String postContent = postDoc.field("content", String.class);
        post.setContent(postContent);

        String postType = postDoc.field("type", String.class);
        post.setType(postType);

        List<String> classNames = Arrays.asList("EdgeConcept", "EdgeNote", "EdgeTutorial", "EdgeQuestion");

        Set<ODocument> inEdges = postDoc.field("in");
        for(ODocument inEdge : inEdges) {
            if(classNames.contains(inEdge.getClassName())) {
                Date createTime = inEdge.field("createTime", Date.class);
                post.setCreateTime(createTime);

                ODocument userDoc = inEdge.field("out");
                User user = UserConverter.buildUser(userDoc);
                post.setUser(user);
                break;
            }
        }

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

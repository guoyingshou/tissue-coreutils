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
        return doc;
    }

    public static Post buildPost(ODocument postDoc) {
        Post post = new Post();
        post.setId(OrientIdentityUtil.encode(postDoc.getIdentity().toString()));

        String postTitle = postDoc.field("title", String.class);
        post.setTitle(postTitle);

        String postContent = postDoc.field("content", String.class);
        post.setContent(postContent);

        String postType = postDoc.field("type", String.class);
        post.setType(postType);

        List<String> labels = Arrays.asList("concept", "note", "tutorial", "question");

        Set<ODocument> inEdges = postDoc.field("in");
        for(ODocument inEdge : inEdges) {
            String label = inEdge.field("label", String.class);
            if(labels.contains(label)) {
                Date createTime = inEdge.field("createTime", Date.class);
                post.setCreateTime(createTime);

                ODocument userDoc = inEdge.field("out");
                User user = UserMapper.buildUser(userDoc);
                post.setUser(user);
                break;
            }
        }

        ODocument planDoc = postDoc.field("plan");
        if(planDoc != null) {
            Plan plan = PlanMapper.buildPlan(planDoc);
            post.setPlan(plan);
        }

        return post;
    }

    public static Post buildPostDetails(ODocument postDoc) {
        Post post = buildPost(postDoc);

        if("question".equals(post.getType())) {
            Question q = new Question(post);
            Set<ODocument> questionCommentsDoc = postDoc.field("comments");
            if(questionCommentsDoc != null) {
                List<QuestionComment> questionComments = QuestionCommentMapper.buildQuestionComments(questionCommentsDoc);
                q.setComments(questionComments);
            }

            Set<ODocument> answersDoc = postDoc.field("answers");
            if(answersDoc != null) {
                List<Answer> answers = AnswerMapper.buildAnswers(answersDoc);
                q.setAnswers(answers);
            }

            return q;
        }
        else {
            Cnt cnt = new Cnt(post);
            Set<ODocument> messagesDoc = postDoc.field("messages");
            if(messagesDoc != null) {
                List<PostMessage> messages = PostMessageMapper.buildPostMessages(messagesDoc);
                cnt.setMessages(messages);
            }
            return cnt;
        }

    }

    public static List<Post> buildPosts(List<ODocument> postsDoc) {
        List<Post> posts = new ArrayList();
        for(ODocument postDoc : postsDoc) {
            Post post = buildPost(postDoc);
            posts.add(post);
        }
        return posts;
    }
}

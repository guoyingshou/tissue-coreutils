package com.tissue.core.converter;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.domain.profile.User;
import com.tissue.domain.plan.Post;
import com.tissue.domain.plan.PostMessage;
import com.tissue.domain.plan.Answer;
import com.tissue.domain.plan.QuestionComment;

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
        if(postDoc == null) {
            return null;
        }

        ODocument userDoc = postDoc.field("user");
        User postUser = UserConverter.buildUser(userDoc);

        Set<ODocument> messagesDoc = postDoc.field("messages");
        List<PostMessage> messages = PostMessageConverter.buildMessages(messagesDoc);

        //for question type post
        Set<ODocument> answersDoc = postDoc.field("answers");
        List<Answer> answers = AnswerConverter.buildAnswers(answersDoc);

        Set<ODocument> questionCommentsDoc = postDoc.field("comments");
        List<QuestionComment> questionComments = QuestionCommentConverter.buildQuestionComments(questionCommentsDoc);

        //construct the post after preprocessing all the necessary fields
        String title = postDoc.field("title", String.class);
        String content = postDoc.field("content", String.class);
        String type = postDoc.field("type", String.class);

        Post post = new Post();
        post.setId(OrientIdentityUtil.encode(postDoc.getIdentity().toString()));
        post.setTitle(title);
        post.setType(type);
        post.setContent(content);

        post.setUser(postUser);
        //non question type post
        post.setMessages(messages);

        //qustion type post
        post.setComments(questionComments);
        post.setAnswers(answers);

        return post;
    }

    public static Post buildMiniumPost(ODocument postDoc) {
        String postTitle = postDoc.field("title", String.class);
        String postType = postDoc.field("type", String.class);
        Date postCreateTime = postDoc.field("createTime", Date.class);

        ODocument postUserDoc = postDoc.field("user");
        User postUser = UserConverter.buildUser(postUserDoc);

        Post post = new Post();
        post.setId(OrientIdentityUtil.encode(postDoc.getIdentity().toString()));
        post.setTitle(postTitle);
        post.setType(postType);
        post.setCreateTime(postCreateTime);
        post.setUser(postUser);

        return post;
    }

    public static List<Post> buildPosts(List<ODocument> postsDoc) {
        List<Post> posts = new ArrayList();
        for(ODocument postDoc : postsDoc) {
            Post post = buildMiniumPost(postDoc);
            posts.add(post);
        }
        return posts;
    }


}

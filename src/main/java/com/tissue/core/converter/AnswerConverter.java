package com.tissue.core.converter;

import com.tissue.core.util.OrientIdentityUtil;

import com.tissue.domain.profile.User;
import com.tissue.domain.plan.Answer;
import com.tissue.domain.plan.AnswerComment;
import com.tissue.domain.plan.Post;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class AnswerConverter {

    public static ODocument convertAnswer(Answer answer) {
        ODocument doc = new ODocument("Answer");
        doc.field("content", answer.getContent());
        doc.field("createTime", answer.getCreateTime());
        doc.field("user", new ORecordId(OrientIdentityUtil.decode(answer.getUser().getId())));
        doc.field("question", new ORecordId(OrientIdentityUtil.decode(answer.getQuestion().getId())));
        return doc;
    }

    public static List<Answer> buildAnswers(Set<ODocument> answersDoc) {
        List<Answer> answers = new ArrayList();
        for(ODocument answerDoc : answersDoc) {
            Answer answer = buildAnswer(answerDoc);
            if(answer != null) 
                answers.add(answer);
        }
        return answers;
    }

    public static Answer buildAnswer(ODocument answerDoc) {
        String answerContent = answerDoc.field("content", String.class);
        Date answerCreateTime = answerDoc.field("createTime", Date.class);

        List<AnswerComment> answerComments = null;
        Set<ODocument> commentsDoc = answerDoc.field("comments");
        if(commentsDoc != null) {
            answerComments = AnswerCommentConverter.buildAnswerComments(commentsDoc);
        }

        Answer answer = new Answer();
        answer.setId(OrientIdentityUtil.encode(answerDoc.getIdentity().toString()));
        answer.setContent(answerContent);
        answer.setCreateTime(answerCreateTime);
        if(answerComments != null) {
            answer.setComments(answerComments); 
        }

        return answer;
    }

    public static Answer buildAnswerWithoutChild(ODocument answerDoc) {
        String answerContent = answerDoc.field("content", String.class);
        Date answerCreateTime = answerDoc.field("createTime", Date.class);

        ODocument userDoc = answerDoc.field("user");
        User user = UserConverter.buildUser(userDoc);

        ODocument postDoc = answerDoc.field("question");
        Post post = PostConverter.buildPostWithoutChild(postDoc);

        Answer answer = new Answer();
        answer.setId(OrientIdentityUtil.encode(answerDoc.getIdentity().toString()));
        answer.setContent(answerContent);
        answer.setCreateTime(answerCreateTime);
        answer.setUser(user);
        answer.setQuestion(post);

        return answer;
    }

}

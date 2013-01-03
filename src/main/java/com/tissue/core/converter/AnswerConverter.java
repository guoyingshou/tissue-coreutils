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
        Answer answer = new Answer();
        answer.setId(OrientIdentityUtil.encode(answerDoc.getIdentity().toString()));

        String answerContent = answerDoc.field("content", String.class);
        answer.setContent(answerContent);

        Date answerCreateTime = answerDoc.field("createTime", Date.class);
        answer.setCreateTime(answerCreateTime);

        Set<ODocument> commentsDoc = answerDoc.field("comments");
        if(commentsDoc != null) {
            List<AnswerComment> answerComments = AnswerCommentConverter.buildAnswerComments(commentsDoc);
            answer.setComments(answerComments); 
        }

        ODocument userDoc = answerDoc.field("user");
        User user = UserConverter.buildUser(userDoc);
        answer.setUser(user);

        return answer;
    }

    public static Answer buildAnswerWithoutChild(ODocument answerDoc) {
        Answer answer = new Answer();
        answer.setId(OrientIdentityUtil.encode(answerDoc.getIdentity().toString()));

        String answerContent = answerDoc.field("content", String.class);
        answer.setContent(answerContent);

        Date answerCreateTime = answerDoc.field("createTime", Date.class);
        answer.setCreateTime(answerCreateTime);

        ODocument userDoc = answerDoc.field("user");
        User user = UserConverter.buildUser(userDoc);
        answer.setUser(user);

        ODocument postDoc = answerDoc.field("question");
        Post post = PostConverter.buildPostWithoutChild(postDoc);
        answer.setQuestion(post);

        return answer;
    }

}

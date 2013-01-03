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

        Answer answer = buildAnswerWithoutChild(answerDoc);

        Set<ODocument> commentsDoc = answerDoc.field("comments");
        if(commentsDoc != null) {
            List<AnswerComment> answerComments = AnswerCommentConverter.buildAnswerComments(commentsDoc);
            answer.setComments(answerComments); 
        }

        return answer;
    }

    public static Answer buildAnswerWithoutChild(ODocument answerDoc) {
        Answer answer = new Answer();
        answer.setId(OrientIdentityUtil.encode(answerDoc.getIdentity().toString()));

        String answerContent = answerDoc.field("content", String.class);
        answer.setContent(answerContent);

        Set<ODocument> inEdges = answerDoc.field("in");
        for(ODocument inEdge : inEdges) {
            if(inEdge.field("label").equals("answer")) {
                Date createTime = inEdge.field("createTime", Date.class);
                answer.setCreateTime(createTime);

                ODocument userDoc = inEdge.field("out");
                User user = UserConverter.buildUser(userDoc);
                answer.setUser(user);
                break;
            }
        }

        ODocument postDoc = answerDoc.field("question");
        Post post = PostConverter.buildPostWithoutChild(postDoc);
        answer.setQuestion(post);

        return answer;
    }

}

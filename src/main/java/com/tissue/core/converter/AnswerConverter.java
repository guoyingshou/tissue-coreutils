package com.tissue.core.converter;

import com.tissue.core.util.OrientIdentityUtil;

import com.tissue.domain.profile.User;
import com.tissue.domain.plan.Answer;
import com.tissue.domain.plan.AnswerComment;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class AnswerConverter {

    public static List<Answer> buildAnswers(Set<ODocument> answersDoc) {
        if(answersDoc == null) {
            return null;
        }

        List<Answer> answers = new ArrayList();
        for(ODocument answerDoc : answersDoc) {
            Answer answer = buildAnswer(answerDoc);
            answers.add(answer);
        }
        return answers;
    }

    public static Answer buildAnswer(ODocument answerDoc) {
        String answerContent = answerDoc.field("content", String.class);
        Date answerCreateTime = answerDoc.field("createTime", Date.class);

        Answer answer = new Answer();
        answer.setId(OrientIdentityUtil.encode(answerDoc.getIdentity().toString()));
        answer.setContent(answerContent);
        answer.setCreateTime(answerCreateTime);

        Set<ODocument> commentsDoc = answerDoc.field("comments");
        List<AnswerComment> answerComments = AnswerCommentConverter.buildAnswerComments(commentsDoc);
        if(answerComments != null) {
            answer.setComments(answerComments); 
        }
 
        return answer;
    }

}

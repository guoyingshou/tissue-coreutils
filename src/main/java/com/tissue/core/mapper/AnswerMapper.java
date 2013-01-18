package com.tissue.core.mapper;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.social.User;
import com.tissue.core.plan.Answer;
import com.tissue.core.plan.AnswerComment;
import com.tissue.core.plan.Post;

import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class AnswerMapper {

    public static ODocument convertAnswer(Answer answer) {
        ODocument doc = new ODocument("Answer");
        doc.field("content", answer.getContent());
        return doc;
    }

    public static List<Answer> buildAnswers(Set<ODocument> answersDoc) {
        List<Answer> answers = new ArrayList();
        for(ODocument answerDoc : answersDoc) {
            String status = answerDoc.field("status", String.class);
            if(status == null) {
                Answer answer = buildAnswerDetails(answerDoc);
                if(answer != null) 
                    answers.add(answer);
            }
        }
        return answers;
    }

    public static Answer buildAnswer(ODocument answerDoc) {
        Answer answer = new Answer();
        answer.setId(OrientIdentityUtil.encode(answerDoc.getIdentity().toString()));

        String answerContent = answerDoc.field("content", String.class);
        answer.setContent(answerContent);

        Set<ODocument> inEdges = answerDoc.field("in");
        for(ODocument inEdge : inEdges) {
            String label = inEdge.field("label", String.class);
            if("answer".equals(label)) {
                Date createTime = inEdge.field("createTime", Date.class);
                answer.setCreateTime(createTime);

                ODocument userDoc = inEdge.field("out");
                User user = UserMapper.buildUser(userDoc);
                answer.setUser(user);
                break;
            }
        }
        ODocument postDoc = answerDoc.field("question");
        Post post = PostMapper.buildPost(postDoc);
        answer.setQuestion(post);
        return answer;
    }

    public static Answer buildAnswerDetails(ODocument answerDoc) {
        Answer answer = buildAnswer(answerDoc);
        Set<ODocument> commentsDoc = answerDoc.field("comments");
        if(commentsDoc != null) {
            List<AnswerComment> answerComments = AnswerCommentMapper.buildAnswerComments(commentsDoc);
            answer.setComments(answerComments); 
        }
        return answer;
    }

}

package com.tissue.core.mapper;

import com.tissue.core.command.PostCommand;
import com.tissue.core.social.Account;
import com.tissue.core.plan.Post;
import com.tissue.core.plan.Question;
import com.tissue.core.plan.QuestionComment;
import com.tissue.core.plan.Answer;
import com.tissue.core.plan.Plan;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.record.OTrackedList;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class QuestionMapper {

    public static ODocument convert(PostCommand command) {
        ODocument doc = new ODocument("Question");
        doc.field("title", command.getTitle());
        doc.field("content", command.getContent());
        doc.field("type", command.getType());
        doc.field("createTime", new Date());
        return doc;
    }

    public static Question buildQuestionSelf(ODocument doc) {
        Question q = new Question();
        q.setId(doc.getIdentity().toString());

        String title = doc.field("title", String.class);
        q.setTitle(title);

        String content = doc.field("content", String.class);
        q.setContent(content);

        String type = doc.field("type", String.class);
        q.setType(type);

        Date createTime = doc.field("createTime", Date.class);
        q.setCreateTime(createTime);

        Boolean deleted = doc.field("deleted", Boolean.class);
        if(deleted != null) {
            q.setDeleted(deleted);
        }
 
        return q;
    }

    public static Question buildQuestion(ODocument doc) {
        Question q = buildQuestionSelf(doc);

        Set<ODocument> inEdgesDoc = doc.field("in");
        for(ODocument inEdgeDoc : inEdgesDoc) {
            ODocument accountDoc = inEdgeDoc.field("out");
            Account account = AccountMapper.buildAccount(accountDoc);
            q.setAccount(account);
        }
        return q;
    }

    public static Question buildQuestionDetails(ODocument doc) {
        Question q = buildQuestion(doc);

        ODocument planDoc = doc.field("plan");
        Plan plan = PlanMapper.buildPlanDetails(planDoc);
        q.setPlan(plan);
 
        List<ODocument> questionCommentsDoc = doc.field("comments");
        if(questionCommentsDoc != null) {
            for(ODocument commentDoc : questionCommentsDoc) {
                String deleted = commentDoc.field("deleted", String.class);
                if(deleted == null) {
                    QuestionComment comment = QuestionCommentMapper.buildQuestionComment(commentDoc);
                    q.addComment(comment);
                }
            }
        }
        List<ODocument> answersDoc = doc.field("answers");
        if(answersDoc != null) {
            for(ODocument answerDoc : answersDoc) {
                String deleted = answerDoc.field("deleted", String.class);
                if(deleted == null) {
                    Answer answer = AnswerMapper.buildAnswerDetails(answerDoc);
                    q.addAnswer(answer);
                }
            }
        }
        return q;
    }

}

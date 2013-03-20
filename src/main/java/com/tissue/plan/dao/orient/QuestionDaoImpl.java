package com.tissue.plan.dao.orient;

import com.tissue.plan.dao.QuestionDao;
import com.tissue.plan.mapper.TopicMapper;
import com.tissue.plan.mapper.PlanMapper;
import com.tissue.plan.mapper.QuestionMapper;
import com.tissue.plan.mapper.QuestionCommentMapper;
import com.tissue.plan.mapper.AnswerMapper;
import com.tissue.plan.mapper.AnswerCommentMapper;
import com.tissue.plan.Topic;
import com.tissue.plan.Plan;
import com.tissue.plan.Question;
import com.tissue.plan.QuestionComment;
import com.tissue.plan.Answer;
import com.tissue.plan.AnswerComment;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class QuestionDaoImpl extends PostDaoImpl implements QuestionDao {

    private static Logger logger = LoggerFactory.getLogger(QuestionDaoImpl.class);

    public Question getQuestion(String id) {
        Question question = null;
        String sql = "select from " + id;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                question = QuestionMapper.buildQuestion(doc);
                ODocument planDoc = doc.field("plan");
                Plan plan = PlanMapper.buildPlan(planDoc);
                question.setPlan(plan);

                ODocument topicDoc = planDoc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                plan.setTopic(topic);

                List<ODocument> plansDoc = topicDoc.field("plans");
                if(plansDoc != null) {
                    for(ODocument topicPlanDoc : plansDoc) {
                        Plan p = PlanMapper.buildPlan(topicPlanDoc);
                        topic.addPlan(p);
                    }
                }

                List<ODocument> commentsDoc = doc.field("comments");
                if(commentsDoc != null) {
                    for(ODocument commentDoc : commentsDoc) {
                        Object deletedDoc = commentDoc.field("deleted");
                        if(deletedDoc == null) {
                            QuestionComment comment = QuestionCommentMapper.buildQuestionComment(commentDoc);
                            question.addComment(comment);
                        }
                    }
                }

                List<ODocument> answersDoc = doc.field("answers");
                if(answersDoc != null) {
                    for(ODocument answerDoc : answersDoc) {
                        Object answerDeletedDoc = answerDoc.field("deleted");
                        if(answerDeletedDoc == null) {
                            Answer answer = AnswerMapper.buildAnswer(answerDoc);
                            question.addAnswer(answer);

                            List<ODocument> answerCommentsDoc = answerDoc.field("comments");
                            if(answerCommentsDoc != null) {
                                for(ODocument answerCommentDoc : answerCommentsDoc) {
                                    Object deletedDoc = answerCommentDoc.field("deleted");
                                    if(deletedDoc == null) {
                                        AnswerComment answerComment = AnswerCommentMapper.buildAnswerComment(answerCommentDoc);
                                        answer.addComment(answerComment);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        finally {
            db.close();
        }
        return question;
    }

}

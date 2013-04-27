package com.tissue.plan.dao.orient;

import com.tissue.core.Account;
import com.tissue.core.mapper.AccountMapper;
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

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class QuestionDaoImpl extends PostDaoImpl implements QuestionDao {

    private static Logger logger = LoggerFactory.getLogger(QuestionDaoImpl.class);

    public Question getQuestion(String id) {
        Question question = null;
        String sql = "select @this as question, in_.out as account, in_.createTime as createTime from " + id;
        logger.debug(sql);

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:4"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                ODocument questionDoc = doc.field("question");
                question = QuestionMapper.buildQuestion(questionDoc);

                ODocument accountDoc = doc.field("account");
                Account account = AccountMapper.buildAccount(accountDoc);
                question.setAccount(account);

                Date ctime0 = doc.field("createTime", Date.class);
                question.setCreateTime(ctime0);

                ODocument planDoc = questionDoc.field("plan");
                Plan plan = PlanMapper.buildPlan(planDoc);
                question.setPlan(plan);

                ODocument topicDoc = planDoc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                TopicMapper.postProcessTopic(topic, topicDoc);
                plan.setTopic(topic);

                List<ODocument> commentsDoc = questionDoc.field("comments");
                if(commentsDoc != null) {
                    for(ODocument commentDoc : commentsDoc) {
                        Object deleted = commentDoc.field("deleted");
                        if(deleted == null) {
                            QuestionComment comment = QuestionCommentMapper.buildQuestionComment(commentDoc);

                            ODocument edge1 = commentDoc.field("in_");
                            Date ctime1 = edge1.field("createTime", Date.class);
                            comment.setCreateTime(ctime1);

                            ODocument commentAccountDoc = edge1.field("out");

                            Account commentAccount = AccountMapper.buildAccount(commentAccountDoc);
                            comment.setAccount(commentAccount);

                            question.addComment(comment);
                        }
                    }
                }

                List<ODocument> answersDoc = questionDoc.field("answers");
                if(answersDoc != null) {
                    for(ODocument answerDoc : answersDoc) {
                        Object deleted = answerDoc.field("deleted");
                        if(deleted == null) {
                            Answer answer = AnswerMapper.buildAnswer(answerDoc);

                            ODocument edge1 = answerDoc.field("in_");
                            Date ctime1 = edge1.field("createTime", Date.class);
                            answer.setCreateTime(ctime1);

                            ODocument answerAccountDoc = edge1.field("out");
                            Account answerAccount = AccountMapper.buildAccount(answerAccountDoc);
                            answer.setAccount(answerAccount);

                            question.addAnswer(answer);

                            List<ODocument> answerCommentsDoc = answerDoc.field("comments");
                            if(answerCommentsDoc != null) {
                                for(ODocument answerCommentDoc : answerCommentsDoc) {
                                    deleted = answerCommentDoc.field("deleted");
                                    if(deleted == null) {
                                        AnswerComment answerComment = AnswerCommentMapper.buildAnswerComment(answerCommentDoc);
                                        ODocument edge2 = answerCommentDoc.field("in_");
                                        Date ctime2 = edge2.field("createTime", Date.class);
                                        answerComment.setCreateTime(ctime2);

                                        ODocument answerCommentAccountDoc = edge2.field("out");
                                        Account answerCommentAccount = AccountMapper.buildAccount(answerCommentAccountDoc);
                                        answerComment.setAccount(answerCommentAccount);

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

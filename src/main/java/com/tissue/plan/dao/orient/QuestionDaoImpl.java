package com.tissue.plan.dao.orient;

import com.tissue.core.Account;
import com.tissue.core.User;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.core.mapper.UserMapper;
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

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;


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
        String sql = "select @this as question, in_EdgeCreatePost.createTime as createTime, in_EdgeCreatePost.out as account, in_EdgeCreatePost.out.out_UserAccounts as user, out_Parent as plan, out_Parent.out_Parent as topic, out('Parent').out('Parent').in('Parent') as topicPlans from " + id;
        logger.debug(sql);

        Question question = null;

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            for(ODocument doc : docs) {
                ODocument questionDoc = doc.field("question");
                question = QuestionMapper.buildQuestion(questionDoc);

                Date createTime = doc.field("createTime", Date.class);
                question.setCreateTime(createTime);

                ODocument accountDoc = doc.field("account");
                Account account = AccountMapper.buildAccount(accountDoc);
                question.setAccount(account);

                ODocument userDoc = doc.field("user");
                User user = UserMapper.buildUser(userDoc);
                account.setUser(user);

                ODocument planDoc = doc.field("plan");
                Plan plan = PlanMapper.buildPlan(planDoc);
                question.setPlan(plan);

                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                plan.setTopic(topic);

                List<ODocument> topicPlanDocs = doc.field("topicPlans");
                for(ODocument topicPlanDoc : topicPlanDocs) {
                    Plan topicPlan = PlanMapper.buildPlan(topicPlanDoc);

                    ODocument topicPlanAccountDoc = topicPlanDoc.field("in_EdgeCreatePlan.out");
                    Account topicPlanAccount = AccountMapper.buildAccount(topicPlanAccountDoc);
                    topicPlan.setAccount(topicPlanAccount);

                    ODocument topicPlanUserDoc = topicPlanAccountDoc.field("out_UserAccounts");
                    User topicPlanUser = UserMapper.buildUser(topicPlanUserDoc);
                    topicPlanAccount.setUser(topicPlanUser);

                    topic.addPlan(topicPlan);
                }

                /**
                List<ODocument> commentsDoc = questionDoc.field("comments");
                if(commentsDoc != null) {
                    for(ODocument commentDoc : commentsDoc) {
                        Object deleted = commentDoc.field("deleted");
                        if(deleted == null) {
                            QuestionComment comment = QuestionCommentMapper.buildQuestionComment(commentDoc);
                            //AccountMapper.setupCreatorAndTimestamp(comment, commentDoc);
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
                            //AccountMapper.setupCreatorAndTimestamp(answer, answerDoc);
                            question.addAnswer(answer);

                            List<ODocument> answerCommentsDoc = answerDoc.field("comments");
                            if(answerCommentsDoc != null) {
                                for(ODocument answerCommentDoc : answerCommentsDoc) {
                                    deleted = answerCommentDoc.field("deleted");
                                    if(deleted == null) {
                                        AnswerComment answerComment = AnswerCommentMapper.buildAnswerComment(answerCommentDoc);
                                        //AccountMapper.setupCreatorAndTimestamp(answerComment, answerCommentDoc);
                                        answer.addComment(answerComment);
                                    }
                                }
                            }
                        }
                    }
                }
                */
            }
        }
        finally {
            db.shutdown();
        }
        return question;
    }

}

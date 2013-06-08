package com.tissue.plan.dao.orient;

import com.tissue.core.Account;
import com.tissue.core.User;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.core.mapper.UserMapper;
import com.tissue.core.dao.orient.ContentDaoImpl;
import com.tissue.plan.command.AnswerCommentCommand;
import com.tissue.plan.mapper.TopicMapper;
import com.tissue.plan.mapper.PlanMapper;
import com.tissue.plan.mapper.QuestionMapper;
import com.tissue.plan.mapper.AnswerMapper;
import com.tissue.plan.mapper.AnswerCommentMapper;
import com.tissue.plan.Topic;
import com.tissue.plan.Plan;
import com.tissue.plan.Question;
import com.tissue.plan.Answer;
import com.tissue.plan.AnswerComment;
import com.tissue.plan.dao.AnswerCommentDao;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.util.List;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class AnswerCommentDaoImpl extends ContentDaoImpl implements AnswerCommentDao {

    private static Logger logger = LoggerFactory.getLogger(AnswerCommentDaoImpl.class);

    public String create(AnswerCommentCommand command) {
        String accountId = command.getAccount().getId();
        String answerId = command.getAnswer().getId();

        OrientGraph db = dataSource.getDB();
        try {
            ODocument doc = AnswerCommentMapper.convertAnswerComment(command);
            doc.save();
            String answerCommentId = doc.getIdentity().toString();

            String sql = "create edge Owns from " + accountId + " to " + answerCommentId + " set category = 'answerComment', createTime = sysdate()";
            logger.debug(sql);

            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "create edge Contains from " + answerId + " to " + answerCommentId;
            logger.debug(sql);

            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();

            return answerId;
        }
        finally {
             db.shutdown();
        }
    }

    public AnswerComment getAnswerComment(String answerCommentId) {
        String sql = "select @this as comment, " +
                     "in_Contains as answer, " +
                     "in_Contains.in_Contains as question, " +
                     "in_Contains.in_Contains.in_Contains as plan, " +
                     "in_Contains.in_Contains.in_Contains.in_Contains as topic " +
                     "from " + answerCommentId;
        logger.debug(sql);

        AnswerComment answerComment = null;

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            for(ODocument doc : docs) {
                ODocument answerCommentDoc = doc.field("comment");
                answerComment = AnswerCommentMapper.buildAnswerComment(answerCommentDoc);

                ODocument answerDoc = doc.field("answer");
                Answer answer = AnswerMapper.buildAnswer(answerDoc);
                answerComment.setAnswer(answer);

                ODocument questionDoc = doc.field("question");
                Question question = QuestionMapper.buildQuestion(questionDoc);
                answer.setQuestion(question);

                ODocument planDoc = doc.field("plan");
                Plan plan = PlanMapper.buildPlan(planDoc);
                question.setPlan(plan);

                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                plan.setTopic(topic);
            }
        }
        finally {
            db.shutdown();
        }
        return answerComment;
    }

}

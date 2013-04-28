package com.tissue.plan.dao.orient;

import com.tissue.core.Account;
import com.tissue.core.mapper.AccountMapper;
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
        String id = null;

        //OGraphDatabase db = dataSource.getDB();
        OrientGraph db = dataSource.getDB();
        try {
            ODocument doc = AnswerCommentMapper.convertAnswerComment(command);
            //db.save(doc);
            doc.save();

            id = doc.getIdentity().toString();
            String userId = command.getAccount().getId();
            String answerId = command.getAnswer().getId();

            String sql = "create edge EdgeCreatePost from " + userId + " to " + id + " set category = 'answerComment', createTime = sysdate()";
            logger.debug(sql);

            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "update " + answerId + " add comments = " + id;
            logger.debug(sql);

            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
             //db.close();
             db.shutdown();
        }

        return id;
    }

    public AnswerComment getAnswerComment(String answerCommentId) {
        String sql = "select from " + answerCommentId;
        logger.debug(sql);

        AnswerComment answerComment = null;

        //OGraphDatabase db = dataSource.getDB();
        OrientGraph db = dataSource.getDB();
        try {
            //List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));

            OCommandSQL cmd = new OCommandSQL(sql);
            List<ODocument> docs = db.command(cmd).execute();

            if(!docs.isEmpty()) {
                ODocument answerCommentDoc = docs.get(0);
                answerComment = AnswerCommentMapper.buildAnswerComment(answerCommentDoc);
                AccountMapper.setupCreatorAndTimestamp(answerComment, answerCommentDoc);

                ODocument answerDoc = answerCommentDoc.field("answer");
                Answer answer = AnswerMapper.buildAnswer(answerDoc);
                answerComment.setAnswer(answer);

                ODocument questionDoc = answerDoc.field("question");
                Question question = QuestionMapper.buildQuestion(questionDoc);
                answer.setQuestion(question);

                ODocument planDoc = questionDoc.field("plan");
                Plan plan = PlanMapper.buildPlan(planDoc);
                question.setPlan(plan);

                ODocument topicDoc = planDoc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                //TopicMapper.setupPlans(topic, topicDoc);
                plan.setTopic(topic);
            }
        }
        finally {
            //db.close();
            db.shutdown();
        }
        return answerComment;
    }

}

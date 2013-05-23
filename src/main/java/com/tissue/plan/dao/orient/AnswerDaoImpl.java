package com.tissue.plan.dao.orient;

import com.tissue.core.Account;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.core.dao.orient.ContentDaoImpl;
import com.tissue.plan.command.AnswerCommand;
import com.tissue.plan.mapper.TopicMapper;
import com.tissue.plan.mapper.PlanMapper;
import com.tissue.plan.mapper.QuestionMapper;
import com.tissue.plan.mapper.AnswerMapper;
import com.tissue.plan.Topic;
import com.tissue.plan.Plan;
import com.tissue.plan.Question;
import com.tissue.plan.Answer;
import com.tissue.plan.dao.AnswerDao;

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
public class AnswerDaoImpl extends ContentDaoImpl implements AnswerDao {

    private static Logger logger = LoggerFactory.getLogger(AnswerDaoImpl.class);

    public String create(AnswerCommand command) {

        String accountId = command.getAccount().getId();
        String questionId = command.getQuestion().getId();

        OrientGraph db = dataSource.getDB();
        try {
            ODocument doc = AnswerMapper.convertAnswer(command);
            doc.save();
            String answerId = doc.getIdentity().toString();

            String sql = "create edge EdgeCreatePost from " + accountId + " to " + answerId + " set category = 'answer', createTime = sysdate()";
            logger.debug(sql);
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "create edge QuestionAnswers from " + answerId + " to " + questionId;
            logger.debug(sql);
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();

            return answerId;
        }
        finally {
            db.shutdown();
        }
    }

    public Answer getAnswer(String answerId) {
        String sql = "select from " + answerId;
        logger.debug(sql);

        Answer answer = null;

        OrientGraph db = dataSource.getDB();
        try {
            List<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            if(!docs.isEmpty()) {
                ODocument answerDoc = docs.get(0);
                answer = AnswerMapper.buildAnswer(answerDoc);
                //AccountMapper.setupCreatorAndTimestamp(answer, answerDoc);

                ODocument questionDoc = answerDoc.field("question");
                Question question = QuestionMapper.buildQuestion(questionDoc);
                answer.setQuestion(question);

                ODocument planDoc = questionDoc.field("plan");
                Plan plan = PlanMapper.buildPlan(planDoc);
                question.setPlan(plan);

                ODocument topicDoc = planDoc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                TopicMapper.setupPlans(topic, topicDoc);
                plan.setTopic(topic);

            }
        }
        finally {
            db.shutdown();
        }
        return answer;
    }

}

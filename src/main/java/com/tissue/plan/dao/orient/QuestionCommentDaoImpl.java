package com.tissue.plan.dao.orient;

import com.tissue.core.dao.orient.ContentDaoImpl;
import com.tissue.plan.dao.QuestionCommentDao;
import com.tissue.plan.command.QuestionCommentCommand;
import com.tissue.plan.mapper.TopicMapper;
import com.tissue.plan.mapper.PlanMapper;
import com.tissue.plan.mapper.QuestionMapper;
import com.tissue.plan.mapper.QuestionCommentMapper;
import com.tissue.plan.Topic;
import com.tissue.plan.Plan;
import com.tissue.plan.Question;
import com.tissue.plan.QuestionComment;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class QuestionCommentDaoImpl extends ContentDaoImpl implements QuestionCommentDao {

    private static Logger logger = LoggerFactory.getLogger(QuestionCommentDaoImpl.class);

    public String create(QuestionCommentCommand command) {
        String id = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = QuestionCommentMapper.convertQuestionComment(command);
            db.save(doc);
        
            id = doc.getIdentity().toString();
            String userId = command.getAccount().getId();
            String qId = command.getQuestion().getId();

            String sql = "create edge EdgeCreate from " + userId + " to " + id + " set label = 'questionComment', createTime = sysdate()";
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "update " + qId + " add comments = " + id;
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
        }
        finally {
            db.close();
        }
        return id;
    }

    public QuestionComment getQuestionComment(String questionCommentId) {
        String sql = "select from " + questionCommentId;
        logger.debug(sql);

        QuestionComment questionComment = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                questionComment = QuestionCommentMapper.buildQuestionComment(doc);

                ODocument questionDoc = doc.field("question");
                Question question = QuestionMapper.buildQuestion(questionDoc);
                questionComment.setQuestion(question);

                ODocument planDoc = questionDoc.field("plan");
                Plan plan = PlanMapper.buildPlan(planDoc);
                question.setPlan(plan);

                ODocument topicDoc = planDoc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                plan.setTopic(topic);
            }
        }
        finally {
           db.close();
        }
        return questionComment;
    }
}

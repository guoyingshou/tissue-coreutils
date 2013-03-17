package com.tissue.core.plan.dao.orient;

import com.tissue.core.util.OrientDataSource;
import com.tissue.core.command.QuestionCommentCommand;
import com.tissue.core.mapper.TopicMapper;
import com.tissue.core.mapper.PlanMapper;
import com.tissue.core.mapper.QuestionMapper;
import com.tissue.core.mapper.QuestionCommentMapper;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Question;
import com.tissue.core.plan.QuestionComment;
import com.tissue.core.plan.dao.QuestionCommentDao;

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
public class QuestionCommentDaoImpl implements QuestionCommentDao {

    private static Logger logger = LoggerFactory.getLogger(QuestionCommentDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public String create(QuestionCommentCommand command) {
        String id = null;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = QuestionCommentMapper.convertQuestionComment(command);
            db.save(doc);
        
            id = doc.getIdentity().toString();
            String userId = command.getAccount().getId();
            String qId = command.getQuestion().getId();

            String sql = "create edge EdgePost from " + userId + " to " + id + " set label = 'questionComment', createTime = sysdate()";
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


    public void update(QuestionCommentCommand command) {
        String sql = "update " + command.getId() + " set content = '" + command.getContent() + "'"; 

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
         }
        finally {
            db.close();
        }
    }
}

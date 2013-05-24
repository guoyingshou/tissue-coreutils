package com.tissue.plan.dao.orient;

import com.tissue.core.Account;
import com.tissue.core.mapper.AccountMapper;
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

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class QuestionCommentDaoImpl extends ContentDaoImpl implements QuestionCommentDao {

    private static Logger logger = LoggerFactory.getLogger(QuestionCommentDaoImpl.class);

    public String create(QuestionCommentCommand command) {

        String questionId = command.getQuestion().getId();
        String accountId = command.getAccount().getId();

        OrientGraph db = dataSource.getDB();
        try {
            ODocument doc = QuestionCommentMapper.convertQuestionComment(command);
            doc.save();
            String questionCommentId = doc.getIdentity().toString();

            String sql = "create edge PostAccount from " + questionCommentId + " to " + accountId + " set category = 'questionComment', createTime = sysdate()";
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "create edge CommentsQuetions from " + questionCommentId + " to " + questionId;
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();

            return questionCommentId;
        }
        finally {
            db.shutdown();
        }
    }

    public QuestionComment getQuestionComment(String questionCommentId) {
        String sql = "select from " + questionCommentId;
        logger.debug(sql);

        QuestionComment questionComment = null;

        OrientGraph db = dataSource.getDB();
        try {
            List<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            if(!docs.isEmpty()) {
                ODocument questionCommentDoc = docs.get(0);
                questionComment = QuestionCommentMapper.buildQuestionComment(questionCommentDoc);
                //AccountMapper.setupCreatorAndTimestamp(questionComment, questionCommentDoc);

                ODocument questionDoc = questionCommentDoc.field("question");
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
           db.shutdown();
        }
        return questionComment;
    }
}

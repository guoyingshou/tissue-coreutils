package com.tissue.core.plan.dao.orient;

//import com.tissue.core.util.OrientDataSource;
import com.tissue.core.mapper.TopicMapper;
import com.tissue.core.mapper.PlanMapper;
import com.tissue.core.mapper.QuestionMapper;
import com.tissue.core.mapper.QuestionCommentMapper;
import com.tissue.core.mapper.AnswerMapper;
import com.tissue.core.mapper.AnswerCommentMapper;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Question;
import com.tissue.core.plan.QuestionComment;
import com.tissue.core.plan.Answer;
import com.tissue.core.plan.AnswerComment;
import com.tissue.core.plan.dao.QuestionDao;

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

    /**
    public String create(QuestionCommand command) {
        String id = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = QuestionMapper.convertQuestion(command);
            db.save(doc);

            id = doc.getIdentity().toString();

            String userId = command.getAccount().getId();
            String planId = command.getPlan().getId();

            String sql = "update " + id + " set plan = " + planId;
            logger.debug(sql);

            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "create edge EdgePost from " + userId + " to " + id + " set createTime = sysdate(), label = 'question'";
            logger.debug(sql);

            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.close();
        }
        return id;
    }

    public void update(QuestionCommand command) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = db.load(new ORecordId(command.getId()));
            doc.field("title", command.getTitle());
            doc.field("content", command.getContent());
            doc.save();
        }
        finally {
            db.close();
        }
    }
    */

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

                List<ODocument> commentsDoc = doc.field("comments");
                if(commentsDoc != null) {
                    for(ODocument commentDoc : commentsDoc) {
                        QuestionComment comment = QuestionCommentMapper.buildQuestionComment(commentDoc);
                        question.addComment(comment);
                    }
                }

                List<ODocument> answersDoc = doc.field("answers");
                if(answersDoc != null) {
                    for(ODocument answerDoc : answersDoc) {
                        Answer answer = AnswerMapper.buildAnswer(answerDoc);

                        List<ODocument> answerCommentsDoc = answerDoc.field("comments");
                        if(answerCommentsDoc != null) {
                            for(ODocument answerCommentDoc : answerCommentsDoc) {
                                AnswerComment answerComment = AnswerCommentMapper.buildAnswerComment(answerCommentDoc);
                                answer.addComment(answerComment);
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

    public long getQuestionsCountByTopic(String topicId) {
        long count = 0;
        String sql = "select count(*) from Question where deleted is null and plan.topic in " + topicId;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                count = doc.field("count", long.class);
            }
        }
        finally {
            db.close();
        }
        return count;
    }

    public List<Question> getPagedQuestionsByTopic(String topicId, int page, int size) {
        List<Question> questions = new ArrayList();

        String sql = "select from Question where deleted is null and plan.topic in " + topicId + " order by createTime desc skip " + ((page - 1) * size) + " limit " + size;


        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                Question question = QuestionMapper.buildQuestion(doc);
                questions.add(question);
            }
        }
        finally {
            db.close();
        }
        return questions;
    }


}

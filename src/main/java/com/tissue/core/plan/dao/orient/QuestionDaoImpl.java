package com.tissue.core.plan.dao.orient;

import com.tissue.core.util.OrientDataSource;
import com.tissue.core.command.QuestionCommand;

import com.tissue.core.mapper.QuestionMapper;
import com.tissue.core.mapper.TopicMapper;
import com.tissue.core.mapper.PlanMapper;
import com.tissue.core.mapper.UserMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Question;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Topic;
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
public class QuestionDaoImpl implements QuestionDao {
    private static Logger logger = LoggerFactory.getLogger(QuestionDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public String create(QuestionCommand command) {
        String id = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = QuestionMapper.convert(command);
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

    public Question getQuestion(String id) {
        Question question = null;
        String sql = "select from " + id;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                question = QuestionMapper.buildQuestionDetails(doc);
            }
        }
        finally {
            db.close();
        }
        return question;
    }

    /**
    public List<Question> getLatestQuestions(int limit) {
        List<Post> posts = new ArrayList();

        OGraphDatabase db = dataSource.getDB();
        try {
            String sql = "select from Post where deleted is null and plan.topic.deleted is null order by createTime desc limit " + limit;
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                Post post = PostMapper.buildPost(doc);
                posts.add(post);
            }
        }
        finally {
            db.close();
        }
        return posts;
    }

    public Topic getTopic(String postId) {
        Topic topic = null;
        String sql = "select plan.topic as topic from " + postId;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                ODocument topicDoc = doc.field("topic");
                topic = TopicMapper.buildTopicDetails(topicDoc);
             }
        }
        finally {
            db.close();
        }
        return topic;
    }

    public String getTopicId(String postId) {
        String id = null;
        String sql = "select plan.topic.@rid as id from " + postId;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                id = doc.field("id", String.class);
             }
        }
        finally {
            db.close();
        }
        return id;
    }
    */

}

package com.tissue.core.plan.dao.orient;

import com.tissue.core.util.OrientDataSource;
import com.tissue.core.command.TopicCommand;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.dao.TopicDao;
import com.tissue.core.mapper.TopicMapper;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.db.record.OTrackedList;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class TopicDaoImpl implements TopicDao {

    private static Logger logger = LoggerFactory.getLogger(TopicDaoImpl.class);

    @Autowired
    protected OrientDataSource dataSource;

    public String create(TopicCommand command) {
        String id = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = TopicMapper.convertTopic(command);
            db.save(doc);

            id = doc.getIdentity().toString();
            String uId = command.getAccount().getId();

            String sql = "create edge EdgePost from " + uId + " to " + id + " set label = 'topic', createTime = sysdate()";
            logger.debug(sql);

            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.close();
        }
        return id;
    }

    public void update(TopicCommand command) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = db.load(new ORecordId(command.getId()));
            doc.field("title", command.getTitle());
            doc.field("content", command.getContent());
            doc.field("tags", command.getTags());
            doc.field("updateTime", new Date());
            db.save(doc);
        }
        finally {
            db.close();
        }
    }

    /**
     * Get a topic with all fields available.
     */
    public Topic getTopic(String topicId) {
        String sql = "select from " + topicId;
        logger.debug(sql);

        Topic topic = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                topic = TopicMapper.buildTopic(doc);
            }
        }
        finally {
           db.close();
        }
        return topic;
    }

    public Topic getTopicByPlan(String planId) {
        String sql = "select topic from " + planId;
        logger.debug(sql);

        Topic topic = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                ODocument topicDoc = doc.field("topic");
                topic = TopicMapper.buildTopic(topicDoc);
            }
        }
        finally {
            db.close();
        }
        return topic;
    }

    /**
     * Get topics with the largest members.
     */
    public List<Topic> getTrendingTopics(int num) {

        String sql = "select topic from Plan where topic.deleted is null order by count desc limit " + num;
        logger.debug(sql);

        List<Topic> topics = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                topics.add(topic);
            }
        }
        finally {
            db.close();
        }
        return topics;
    }

    /**
     * Get featured topics.
     */
    public List<Topic> getFeaturedTopics(int num) {

        String sql = "select from Topic where deleted is null and type = 'featured' order by createTime desc limit " + num;
        logger.debug(sql);

        List<Topic> topics = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {

            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                topics.add(topic);
            }
        }
        finally {
            db.close();
        }
        return topics;
    }

    /**
     * Get topics count.
     */
    public long getTopicsCount() {
        long count = 0L;
        OGraphDatabase db = dataSource.getDB();
        try {
            count = db.countClass("topic");
        }
        finally {
            db.close();
        }
        return count;
    }

    /**
     * Get paged topics reverse ordered by createTime.
     */
    public List<Topic> getPagedTopics(int page, int size) {

        String sql = "select from Topic where deleted is null order by createTime desc skip " + ((page -1) * size) + " limit " + size;
        logger.debug(sql);

        List<Topic> topics = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                Topic topic = TopicMapper.buildTopic(doc);
                topics.add(topic);
            }
        }
        finally {
            db.close();
        }
        return topics;
    }

    /**
     * Get all tags.
     */
    public List<String> getTopicTags() {
        String sql = "select set(tags) from Topic where deleted is null";
        logger.debug(sql);

        OTrackedList<String> tags = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if((docs != null) && (docs.size() > 0)) {
                tags = docs.get(0).field("set");
            }
            return tags;
        }
        finally {
            db.close();
        }
    }

    public long getTopicsCountByTag(String tag) {

        String sql = "select count(*) from Topic where deleted is null and tags in " + tag;
        logger.debug(sql);

        long result = 0L;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(docs.size() > 0) {
                ODocument countDoc = docs.get(0);
                result = countDoc.field("count", long.class);
            }
            return result;
        }
        finally {
            db.close();
        }
    }

    public List<Topic> getPagedTopicsByTag(String tag, int page, int size) {

        String sql = "select from Topic where deleted is null and tags in '" + tag + "' order by createTime desc skip " + (page - 1) * size + " limit " + size;
        logger.debug(sql);

        List<Topic> topics = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                Topic topic = TopicMapper.buildTopic(doc);
                topics.add(topic);
            }
        }
        finally {
           db.close();
        }
        return topics;
    }

    /**
     * Questions
     */
    public List<Topic> getNewTopics(String excludingUserId, int limit) {
        String sql = "select from topic where deleted is null order by createTime desc limit " + limit;
        if(excludingUserId != null) {
            sql = "select from topic where deleted is null and in.out not in " + excludingUserId + " and plans.in.out not in " + excludingUserId + " order by createTime desc limit " + limit;
        }
        logger.debug(sql);

        List<Topic> topics = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                Topic topic = TopicMapper.buildTopic(doc);
                topics.add(topic);
            }
        }
        finally {
            db.close();
        }
        return topics;
    }

}

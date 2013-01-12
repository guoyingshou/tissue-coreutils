package com.tissue.core.plan.dao.orient;

import com.tissue.core.util.OrientDataSource;
import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.mapper.TopicMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Post;
import com.tissue.core.plan.dao.TopicDao;
import com.tissue.core.plan.dao.PostDao;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.record.OTrackedList;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.id.ORecordId;

@Component
public class TopicDaoImpl implements TopicDao {

    @Autowired
    private OrientDataSource dataSource;

    /**
     * Add a topic.
     */
    public Topic create(Topic topic) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = TopicMapper.convertTopic(topic);
            doc.save();

            String ridTopic = doc.getIdentity().toString();
            String ridUser = OrientIdentityUtil.decode(topic.getUser().getId());

            String sql = "create edge EdgeCreate from " + ridUser + " to " + ridTopic + " set label = 'create', createTime = sysdate()";
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();

            String topicId = OrientIdentityUtil.encode(ridTopic);
            topic.setId(topicId);
        }
        catch(Exception exc) {
            //to do
            exc.printStackTrace();
        }
        finally {
            db.close();
        }

        return topic;
    }

    /**
     * Update a topic.
     */
    public void update(Topic topic) {
        /**
        String ridTopic = OrientIdentityUtil.decode(topic.getId());
        String sql = "update " + ridTopic + " set content = " + topic.getContent() + ", tags = " + topic.getTags();
        */

        OGraphDatabase db = dataSource.getDB();
        try {
            /**
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
            */

            ODocument doc = db.load(new ORecordId(OrientIdentityUtil.decode(topic.getId())));
            doc.field("content", topic.getContent());
            doc.field("tags", topic.getTags());
            doc.save();
        }
        catch(Exception exc) {
            //to do
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
    }

    /**
     * Get a topic with all fields available.
     */
    public Topic getTopic(String topicId) {
        Topic topic = null;

        String sql = "select from " + OrientIdentityUtil.decode(topicId);
        OGraphDatabase db = dataSource.getDB();
        try {
            OSQLSynchQuery<ODocument> q = new OSQLSynchQuery(sql);
            List<ODocument> result = db.query(q.setFetchPlan("*:-1"));
            if(result.size() > 0) {
                ODocument doc = result.get(0);
                topic = TopicMapper.buildTopic(doc);
            }
        }
        catch(Exception exc) {
            exc.printStackTrace();
            //to do
        }
        finally {
            db.close();
        }
        return topic;
    }

    /**
     * Get a topic by plan id with all fields available.
     */
    public Topic getTopicByPlanId(String planId) {
        Topic topic = null;

        String sql = "select topic from " + OrientIdentityUtil.decode(planId);
        OGraphDatabase db = dataSource.getDB();
        try {
            OSQLSynchQuery<ODocument> q = new OSQLSynchQuery(sql);
            List<ODocument> result = db.query(q.setFetchPlan("*:-1"));
            if(result.size() > 0) {
                ODocument doc = result.get(0);
                ODocument topicDoc = doc.field("topic");
                topic = TopicMapper.buildTopic(topicDoc);
            }
        }
        catch(Exception exc) {
            exc.printStackTrace();
            //to do
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
        List<Topic> topics = new ArrayList();

        String qstr = "select topic, count from Plan order by count desc limit " + num;

        OGraphDatabase db = dataSource.getDB();
        try {
            OSQLSynchQuery query = new OSQLSynchQuery(qstr);
            List<ODocument> docs = db.query(query);
            for(ODocument doc : docs) {
                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopicWithoutChild(topicDoc);
                topics.add(topic);
            }
        }
        catch(Exception exc) {
            //to do
            exc.printStackTrace();
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
        List<Topic> topics = new ArrayList();

        String qstr = "select from Topic where type = 'featured' order by createTime desc limit " + num;

        OGraphDatabase db = dataSource.getDB();
        try {
            OSQLSynchQuery query = new OSQLSynchQuery(qstr);
            List<ODocument> docs = db.query(query);
            for(ODocument doc : docs) {
                Topic topic = TopicMapper.buildTopicWithoutChild(doc);
                topics.add(topic);
            }
        }
        catch(Exception exc) {
            //to do
            exc.printStackTrace();
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
        long result = 0;

        OGraphDatabase db = dataSource.getDB();
        try {
            result = db.countClass("Topic");
        }
        catch(Exception exc) {
            //to do
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return result;
    }

    /**
     * Get paged topics reverse ordered by createTime.
     */
    public List<Topic> getPagedTopics(int page, int size) {
        List<Topic> topics = new ArrayList();

        String qstr = "select from Topic order by createTime desc skip " + ((page -1) * size) + " limit " + size;

        OGraphDatabase db = dataSource.getDB();
        try {
            OSQLSynchQuery query = new OSQLSynchQuery(qstr);
            List<ODocument> docs = db.query(query);
            for(ODocument doc : docs) {
                Topic topic = TopicMapper.buildTopicWithoutChild(doc);
                topics.add(topic);
            }
        }
        catch(Exception exc) {
            //to do
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return topics;
    }

    /**
     * Get all topics reverse ordered by createTime.
     */
    public List<Topic> getTopics() {
        List<Topic> topics = new ArrayList();

        String qstr = "select from Topic order by createTime desc";

        OGraphDatabase db = dataSource.getDB();
        try {
            OSQLSynchQuery query = new OSQLSynchQuery(qstr);
            List<ODocument> docs = db.query(query);
            for(ODocument doc : docs) {
                Topic topic = TopicMapper.buildTopicWithoutChild(doc);
                topics.add(topic);
            }
        }
        catch(Exception exc) {
            //to do
            exc.printStackTrace();
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
        OTrackedList<String> tags = null;

        String qstr = "select set(tags) from Topic";

        OGraphDatabase db = dataSource.getDB();
        try {
            OSQLSynchQuery query = new OSQLSynchQuery(qstr);
            List<ODocument> docs = db.query(query);
            if((docs != null) && (docs.size() > 0)) {
                tags = docs.get(0).field("set");
            }
        }
        catch(Exception exc) {
            //to do
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return tags;
    }

    public long getTopicsCountByTag(String tag) {
        long result = 0L;

        String qstr = "select count(*) from Topic where tags in ?";

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(qstr);
            List<ODocument> docs = db.command(cmd).execute(tag);
            if(docs.size() > 0) {
                ODocument countDoc = docs.get(0);
                result = countDoc.field("count", long.class);
            }
        }
        catch(Exception exc) {
            //to do
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return result;
    }

    public List<Topic> getPagedTopicsByTag(String tag, int page, int size) {
        List<Topic> topics = new ArrayList();

        String qstr = "select from Topic where tags in ? order by createTime desc skip " + 
                       (page - 1) * size +
                       " limit " + size;

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(qstr);
            List<ODocument> docs = db.command(cmd).execute(tag);
            for(ODocument doc : docs) {
                Topic topic = TopicMapper.buildTopicWithoutChild(doc);
                topics.add(topic);
            }
        }
        catch(Exception exc) {
            //to do
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return topics;
    }

    public List<Topic> getTopicsByTag(String tag) {
        List<Topic> topics = new ArrayList();

        String qstr = "select from Topic where tags in ? order by createTime desc";

        OGraphDatabase db = dataSource.getDB();
        try {
            OCommandSQL cmd = new OCommandSQL(qstr);
            List<ODocument> docs = db.command(cmd).execute(tag);
            for(ODocument doc : docs) {
                Topic topic = TopicMapper.buildTopicWithoutChild(doc);
                topics.add(topic);
            }
        }
        catch(Exception exc) {
            //to do
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return topics;
    }

    public void addPlan(Plan plan) {
        OGraphDatabase db = dataSource.getDB();
        try {

            ORecordId topicRecord = new ORecordId(OrientIdentityUtil.decode(plan.getTopic().getId()));
            ORecordId planRecord = new ORecordId(OrientIdentityUtil.decode(plan.getId()));

            ODocument topicDoc = db.load(topicRecord);
            Set<ORecordId> plans = topicDoc.field("plans", Set.class);
            if(plans == null) {
                plans = new HashSet();
            }
            plans.add(planRecord);
            topicDoc.field("plans", plans);
            topicDoc.save();
        }
        catch(Exception exc) {
            //to do
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
    }
}

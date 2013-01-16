package com.tissue.core.plan.dao.orient;

import com.tissue.core.orient.dao.OrientDao;
import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.social.User;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Post;
import com.tissue.core.plan.dao.TopicDao;
import com.tissue.core.plan.dao.PostDao;
import com.tissue.core.mapper.TopicMapper;
import com.tissue.core.mapper.PlanMapper;

import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.record.OTrackedList;

@Component
public class TopicDaoImpl extends OrientDao implements TopicDao {

    /**
     * Add a topic.
     */
    public Topic create(Topic topic) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = TopicMapper.convertTopic(topic);
            saveDoc(doc);

            String ridTopic = doc.getIdentity().toString();
            String ridUser = OrientIdentityUtil.decode(topic.getUser().getId());

            String sql = "create edge EdgeCreate from " + ridUser + " to " + ridTopic + " set label = 'create', createTime = sysdate()";
            executeCommand(db, sql);
            topic.setId(OrientIdentityUtil.encode(ridTopic));
            return topic;
        }
        finally {
            db.close();
        }
    }

    /**
     * Update a topic.
     */
    public void update(Topic topic) {

        String ridTopic = OrientIdentityUtil.decode(topic.getId());
        String sql = "update " + ridTopic + " set content = '" + topic.getContent() + "', tags = " + topic.getTags().toString();

        OGraphDatabase db = dataSource.getDB();
        try {
            executeCommand(db, sql);
        }
        finally {
            db.close();
        }
    }

    /**
     * Get a topic with all fields available.
     */
    public Topic getTopic(String topicId) {
        String rid = OrientIdentityUtil.decode(topicId);
        String sql = "select from " + rid;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = querySingle(db, sql);
            return TopicMapper.buildTopicWithPlans(doc);
        }
        finally {
           db.close();
        }
    }

    /**
     * Get a topic by plan id with all fields available.
     */
    public Topic getTopicByPlanId(String planId) {

        String sql = "select topic from " + OrientIdentityUtil.decode(planId);

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = querySingle(db, sql);
            return TopicMapper.buildTopic(doc);
        }
        finally {
            db.close();
        }
    }


    /**
     * Get topics with the largest members.
     */
    public List<Topic> getTrendingTopics(int num) {
        List<ODocument> topicsDoc = new ArrayList();

        String sql = "select topic, count from Plan order by count desc limit " + num;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            for(ODocument doc : docs) {
                ODocument topicDoc = doc.field("topic");
                if(topicDoc != null) {
                    topicsDoc.add(topicDoc);
                }
            }
            return TopicMapper.buildTopics(topicsDoc);
        }
        finally {
            db.close();
        }
    }

    /**
     * Get featured topics.
     */
    public List<Topic> getFeaturedTopics(int num) {

        String sql = "select from Topic where type = 'featured' order by createTime desc limit " + num;

        OGraphDatabase db = dataSource.getDB();
        try {
        List<ODocument> docs = query(db, sql);
        return TopicMapper.buildTopics(docs);
        }
        finally {
            db.close();

        }
    }

    /**
     * Get topics count.
     */
    public long getTopicsCount() {
        return countClass("Topic");
    }

    /**
     * Get paged topics reverse ordered by createTime.
     */
    public List<Topic> getPagedTopics(int page, int size) {

        //String sql = "select @rid, title, content, in[label='create'].createTime, in.out[@class='User'].@rid as ridUser, in.out[@class='User'].displayName as displayName from Topic order by createTime desc skip " + ((page -1) * size) + " limit " + size;
        String sql = "select from Topic order by createTime desc skip " + ((page -1) * size) + " limit " + size;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            return TopicMapper.buildTopics(docs);
        }
        finally {
            db.close();
        }
    }

    /**
     * Get all topics reverse ordered by createTime.
     */
    public List<Topic> getTopics() {
        String sql = "select from Topic order by createTime desc";


        OGraphDatabase db = dataSource.getDB();
        try {
        List<ODocument> docs = query(db, sql);
        return TopicMapper.buildTopics(docs);
        }
        finally {
            db.close();
        }
    }

    /**
     * Get all tags.
     */
    public List<String> getTopicTags() {
        OTrackedList<String> tags = null;
        String sql = "select set(tags) from Topic";

        OGraphDatabase db = dataSource.getDB();
        try {

        List<ODocument> docs = query(db, sql);
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
        long result = 0L;

        String sql = "select count(*) from Topic where tags in " + tag;
        OGraphDatabase db = dataSource.getDB();
        try {


        List<ODocument> docs = query(db, sql);
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
        String sql = "select from Topic where tags in " + tag + " order by createTime desc skip " + (page - 1) * size + " limit " + size;

        OGraphDatabase db = dataSource.getDB();
        try {


        List<ODocument> docs = query(db, sql);
        return TopicMapper.buildTopics(docs);
        }
        finally {
           db.close();
        }
    }

    public List<Topic> getTopicsByTag(String tag) {
        String sql = "select from Topic where tags in " + tag + " order by createTime desc";

        OGraphDatabase db = dataSource.getDB();
        try {


        List<ODocument> docs = query(db, sql);
        return TopicMapper.buildTopics(docs);
        }
        finally {
            db.close();
        }
    }

    /**
    public void addPlan(Plan plan) {

        OGraphDatabase db = dataSource.getDB();
        try {

        ODocument planDoc = PlanMapper.convertPlan(plan);
        saveDoc(planDoc);
        System.out.println("in topic dao: " + planDoc);

        String ridPlan = planDoc.getIdentity().toString();

        String ridTopic = OrientIdentityUtil.decode(plan.getTopic().getId());
        String ridUser = OrientIdentityUtil.decode(plan.getUser().getId());

        String sql= "create edge EdgeHost from " + ridUser + " to " + ridPlan + " set label = 'host', createTime = sysdate()";
        String sql2 = "update " + ridPlan + " set topic = " + ridTopic;
        String sql3 = "update " + ridTopic + " add plans = " + ridPlan;

        executeCommand(db, sql);
        executeCommand(db, sql2);
        executeCommand(db, sql3);
        }
        finally {
            db.close();
        }
    }
    */
}

package com.tissue.core.plan.dao.orient;

import com.tissue.core.command.TopicCommand;
import com.tissue.core.util.OrientDataSource;

import com.tissue.core.social.User;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Post;
import com.tissue.core.plan.dao.TopicDao;
import com.tissue.core.plan.dao.PostDao;
import com.tissue.core.mapper.TopicMapper;
import com.tissue.core.mapper.PlanMapper;
import com.tissue.core.mapper.UserMapper;

import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.db.record.OTrackedList;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

@Component
//public class TopicDaoImpl extends OrientDao implements TopicDao {
public class TopicDaoImpl implements TopicDao {

    @Autowired
    protected OrientDataSource dataSource;

    /**
     * Add a topic.
     */
    public String create(TopicCommand command) {
        String id = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = TopicMapper.convertTopic(command);
            db.save(doc);
            //saveDoc(doc);

            id = doc.getIdentity().toString();
            String uId = command.getUser().getId();

            String sql = "create edge EdgePost from " + uId + " to " + id + " set label = 'topic', createTime = sysdate()";
            //executeCommand(db, sql);
            OCommandSQL cmd = new OCommandSQL(sql);
            //Object result = db.command(cmd).execute();
            db.command(cmd).execute();
 
            //topicId = OrientIdentityUtil.encode(ridTopic);
        }
        finally {
            db.close();
        }
        return id;
    }

    /**
     * Update a topic.
     */
    public void update(TopicCommand command) {
        //String ridTopic = OrientIdentityUtil.decode(command.getId());
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = db.load(new ORecordId(command.getId()));
            doc.field("title", command.getTitle());
            doc.field("content", command.getContent());
            doc.field("tags", command.getTags());
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
        Topic topic = null;

        //String rid = OrientIdentityUtil.decode(topicId);
        String sql = "select from " + topicId;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                topic = TopicMapper.buildTopicDetails(doc);
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
        List<Topic> topics = new ArrayList();

        String sql = "select topic from Plan order by count desc limit " + num;
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
        List<Topic> topics = new ArrayList();

        String sql = "select from Topic where type = 'featured' order by createTime desc limit " + num;
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
        List<Topic> topics = new ArrayList();

        String sql = "select from Topic order by createTime desc skip " + ((page -1) * size) + " limit " + size;
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
        OTrackedList<String> tags = null;
        String sql = "select set(tags) from Topic";

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
        long result = 0L;

        String sql = "select count(*) from Topic where tags in " + tag;
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
        List<Topic> topics = new ArrayList();

        String sql = "select from Topic where tags in '" + tag + "' order by createTime desc skip " + (page - 1) * size + " limit " + size;

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

    public List<Topic> getNewTopics(String excludingUserId, int limit) {
        List<Topic> topics = new ArrayList();

        String sql = "select from topic order by createTime desc limit " + limit;
        if(excludingUserId != null) {
            //String rid = OrientIdentityUtil.decode(excludingUserId);
            sql = "select from topic where in.out not in " + excludingUserId + " and plans.in.out not in " + excludingUserId + " order by createTime desc limit " + limit;
        }

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

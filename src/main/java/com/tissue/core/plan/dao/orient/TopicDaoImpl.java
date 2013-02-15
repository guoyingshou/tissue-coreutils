package com.tissue.core.plan.dao.orient;

import com.tissue.core.orient.dao.OrientDao;
import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.plan.command.TopicCommand;
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

@Component
public class TopicDaoImpl extends OrientDao implements TopicDao {

    /**
     * Add a topic.
     */
    public Topic create(TopicCommand command) {
        Topic topic = null;
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = TopicMapper.convertTopic(command);
            saveDoc(doc);

            String ridTopic = doc.getIdentity().toString();
            String ridUser = OrientIdentityUtil.decode(command.getUser().getId());

            String sql = "create edge EdgePost from " + ridUser + " to " + ridTopic + " set label = 'topic', createTime = sysdate()";
            executeCommand(db, sql);

            topic = new Topic();
            topic.setId(OrientIdentityUtil.encode(ridTopic));
            topic.setTitle(command.getTitle());
            topic.setContent(command.getContent());
            topic.setTags(command.getTags());
            topic.setUser(command.getUser());
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
    public void update(TopicCommand command) {
        String ridTopic = OrientIdentityUtil.decode(command.getId());
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = db.load(new ORecordId(ridTopic));
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

        String rid = OrientIdentityUtil.decode(topicId);
        String sql = "select from " + rid;

        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = querySingle(db, sql);
            topic = TopicMapper.buildTopicDetails(doc);
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
     * Get topics with the largest members.
     */
    public List<Topic> getTrendingTopics(int num) {
        List<Topic> topics = new ArrayList();

        String sql = "select topic from Plan order by count desc limit " + num;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);

            for(ODocument doc : docs) {
                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
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

        String sql = "select from Topic where type = 'featured' order by createTime desc limit " + num;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            for(ODocument doc : docs) {
                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                topics.add(topic);
            }
        }
        catch(Exception exc) {
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
        return countClass("Topic");
    }

    /**
     * Get paged topics reverse ordered by createTime.
     */
    public List<Topic> getPagedTopics(int page, int size) {
        List<Topic> topics = new ArrayList();

        String sql = "select from Topic order by createTime desc skip " + ((page -1) * size) + " limit " + size;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            for(ODocument doc : docs) {
                Topic topic = TopicMapper.buildTopic(doc);
                topics.add(topic);
            }
        }
        catch(Exception exc) {
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
        List<Topic> topics = new ArrayList();

        String sql = "select from Topic where tags in '" + tag + "' order by createTime desc skip " + (page - 1) * size + " limit " + size;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            for(ODocument doc : docs) {
                Topic topic = TopicMapper.buildTopic(doc);
                topics.add(topic);
            }
        }
        catch(Exception exc) {
            exc.printStackTrace();
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
            String rid = OrientIdentityUtil.decode(excludingUserId);
            sql = "select from topic where in.out not in " + rid + " and plans.in.out not in " + rid + " order by createTime desc limit " + limit;
        }

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            for(ODocument doc : docs) {
                Topic topic = TopicMapper.buildTopic(doc);
                topics.add(topic);
            }
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return topics;
    }


}

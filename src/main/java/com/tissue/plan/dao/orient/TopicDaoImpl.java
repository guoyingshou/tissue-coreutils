package com.tissue.plan.dao.orient;

import com.tissue.core.Account;
import com.tissue.core.dao.orient.ContentDaoImpl;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.plan.dao.TopicDao;
import com.tissue.plan.command.TopicCommand;
import com.tissue.plan.mapper.TopicMapper;
import com.tissue.plan.mapper.PlanMapper;
import com.tissue.plan.Topic;
import com.tissue.plan.Plan;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class TopicDaoImpl extends ContentDaoImpl implements TopicDao {

    private static Logger logger = LoggerFactory.getLogger(TopicDaoImpl.class);

    public String create(TopicCommand command) {
        String id = null;

        //OGraphDatabase db = dataSource.getDB();
        OrientGraph db = dataSource.getDB();
        try {
            ODocument doc = TopicMapper.convertTopic(command);
            doc.save();
            //db.save(doc);

            id = doc.getIdentity().toString();
            String accountId = command.getAccount().getId();

            String sql = "create edge EdgeCreateTopic from " + accountId + " to " + id + " set category = 'topic', createTime = sysdate()";
            logger.debug(sql);

            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            //db.close();
            db.shutdown();
        }
        return id;
    }

    /**
     * Get a topic with all fields available.
     */
    public Topic getTopic(String topicId) {
        //String sql = "select @this as topic, in_.out as account, in_.createTime as createTime from " + topicId;
        String sql = "select from " + topicId;
        logger.debug(sql);

        Topic topic = null;

        //OGraphDatabase db = dataSource.getDB();
        OrientGraph db = dataSource.getDB();
        try {
//            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));

            OCommandSQL cmd = new OCommandSQL(sql);
            List<ODocument> docs = db.command(cmd).execute();

            if(!docs.isEmpty()) {
                ODocument topicDoc = docs.get(0);
                topic = TopicMapper.buildTopic(topicDoc);

                AccountMapper.setupCreatorAndTimestamp(topic, topicDoc);
                TopicMapper.setupPlans(topic, topicDoc);
            }
        }
        finally {
           //db.close();
           db.shutdown();
        }
        return topic;
    }

    public Topic getTopicByPlan(String planId) {
        String sql = "select topic from " + planId;
        logger.debug(sql);

        Topic topic = null;

        //OGraphDatabase db = dataSource.getDB();
        OrientGraph db = dataSource.getDB();
        try {
//            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            OCommandSQL cmd = new OCommandSQL(sql);
            List<ODocument> docs = db.command(cmd).execute();


            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                ODocument topicDoc = doc.field("topic");
                topic = TopicMapper.buildTopic(topicDoc);

                //TopicMapper.setupCreatorAndTimestamp(topic, topicDoc);
                TopicMapper.setupPlans(topic, topicDoc);
            }
        }
        finally {
            //db.close();
            db.shutdown();
        }
        return topic;
    }

    public Topic getTopicByPost(String postId) {
        String sql = "select plan.topic as topic from " + postId;
        logger.debug(sql);

        Topic topic = null;

        //OGraphDatabase db = dataSource.getDB();
        OrientGraph db = dataSource.getDB();
        try {
//            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            OCommandSQL cmd = new OCommandSQL(sql);
            List<ODocument> docs = db.command(cmd).execute();


            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                ODocument topicDoc = doc.field("topic");
                topic = TopicMapper.buildTopic(topicDoc);

                TopicMapper.setupPlans(topic, topicDoc);
             }
        }
        finally {
            //db.close();
            db.shutdown();
        }
        return topic;
    }

    public void update(TopicCommand command) {
        //OGraphDatabase db = dataSource.getDB();
        OrientGraph db = dataSource.getDB();
        try {
            OrientVertex v = db.getVertex(command.getId());
            v.setProperty("title", command.getTitle());
            v.setProperty("content", command.getContent());
            v.setProperty("tags", command.getTags());
 
            /**
            ODocument doc = db.load(new ORecordId(command.getId()));
            doc.field("title", command.getTitle());
            doc.field("content", command.getContent());
            doc.field("tags", command.getTags());
            doc.field("updateTime", new Date());
            db.save(doc);
            */
        }
        finally {
            //db.close();
            db.shutdown();
        }
    }

    /**
     * Get topics with the largest members.
     */
    public List<Topic> getTrendingTopics(int num) {
        String sql = "select in_.size() as memberCount, topic, topic.in_.out as account, topic.in_.createTime as createTime from Plan where topic.deleted is null order by memberCount desc limit " + num;
        logger.debug(sql);

        List<Topic> topics = new ArrayList();

        //OGraphDatabase db = dataSource.getDB();
        OrientGraph db = dataSource.getDB();
        try {
//            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            OCommandSQL cmd = new OCommandSQL(sql);
            List<ODocument> docs = db.command(cmd).execute();


            for(ODocument doc : docs) {
                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);

                Date createTime = doc.field("createTime", Date.class);
                topic.setCreateTime(createTime);

                ODocument accountDoc = doc.field("account");
                Account account = AccountMapper.buildAccount(accountDoc);
                topic.setAccount(account);

                topics.add(topic);
            }
        }
        finally {
            //db.close();
            db.shutdown();
        }
        return topics;
    }

    /**
     * Get featured topics.
     */
    public List<Topic> getFeaturedTopics(int num) {

        String sql = "select in as topic, out as account, createTime from EdgeCreateTopic where in.deleted is null and in.type = 'featured' order by createTime desc limit " + num;
        logger.debug(sql);

        List<Topic> topics = new ArrayList();

        //OGraphDatabase db = dataSource.getDB();
        OrientGraph db = dataSource.getDB();
        try {
//            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            OCommandSQL cmd = new OCommandSQL(sql);
            List<ODocument> docs = db.command(cmd).execute();


            for(ODocument doc : docs) {
                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);

                Date createTime = doc.field("createTime", Date.class);
                topic.setCreateTime(createTime);

                ODocument accountDoc = doc.field("account");
                Account account = AccountMapper.buildAccount(accountDoc);
                topic.setAccount(account);

                topics.add(topic);
            }
        }
        finally {
            //db.close();
            db.shutdown();
        }
        return topics;
    }

    /**
     * Get topics count.
     */
    public long getTopicsCount() {
        long count = 0L;

        //OGraphDatabase db = dataSource.getDB();
        OrientGraph db = dataSource.getDB();
        try {
            //count = db.countClass("topic");
            count = db.countVertices("topic");
        }
        finally {
            //db.close();
            db.shutdown();
        }
        return count;
    }

    /**
     * Get paged topics reverse ordered by createTime.
     */
    public List<Topic> getPagedTopics(int page, int size) {

        String sql = "select in as topic, out as account, createTime from EdgeCreateTopic where in.deleted is null order by createTime desc skip " + ((page -1) * size) + " limit " + size;
        logger.debug(sql);

        List<Topic> topics = new ArrayList();

        //OGraphDatabase db = dataSource.getDB();
        OrientGraph db = dataSource.getDB();
        try {
//            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            OCommandSQL cmd = new OCommandSQL(sql);
            List<ODocument> docs = db.command(cmd).execute();


            for(ODocument doc : docs) {
                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);

                Date createTime = doc.field("createTime", Date.class);
                topic.setCreateTime(createTime);

                ODocument accountDoc = doc.field("account");
                Account account = AccountMapper.buildAccount(accountDoc);
                topic.setAccount(account);

                topics.add(topic);
            }
        }
        finally {
            //db.close();
            db.shutdown();
        }
        return topics;
    }

    /**
     * Get all tags.
     */
    public Set<String> getTopicTags() {
        String sql = "select set(tags) as tags from Topic where deleted is null";
        logger.debug(sql);

        Set<String> tags = new TreeSet<String>();

        //OGraphDatabase db = dataSource.getDB();
        OrientGraph db = dataSource.getDB();
        try {
//            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            OCommandSQL cmd = new OCommandSQL(sql);
            List<ODocument> docs = db.command(cmd).execute();


            if((docs != null) && (docs.size() > 0)) {
                Set<String> result = docs.get(0).field("tags", Set.class);
                tags.addAll(result);
            }
            return tags;
        }
        finally {
            //db.close();
            db.shutdown();
        }
    }

    public long getTopicsCountByTag(String tag) {

        String sql = "select count(*) from Topic where deleted is null and tags in " + tag;
        logger.debug(sql);

        long result = 0L;

        //OGraphDatabase db = dataSource.getDB();
        OrientGraph db = dataSource.getDB();
        try {
//            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            OCommandSQL cmd = new OCommandSQL(sql);
            List<ODocument> docs = db.command(cmd).execute();


            if(docs.size() > 0) {
                ODocument countDoc = docs.get(0);
                result = countDoc.field("count", long.class);
            }
            return result;
        }
        finally {
            //db.close();
            db.shutdown();
        }
    }

    public List<Topic> getPagedTopicsByTag(String tag, int page, int size) {

        String sql = "select in as topic, out as account, createTime from EdgeCreateTopic where in.deleted is null and in.tags in '" + tag + "' order by createTime desc skip " + (page - 1) * size + " limit " + size;
        logger.debug(sql);

        List<Topic> topics = new ArrayList();

        //OGraphDatabase db = dataSource.getDB();
        OrientGraph db = dataSource.getDB();
        try {
//            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            OCommandSQL cmd = new OCommandSQL(sql);
            List<ODocument> docs = db.command(cmd).execute();



            for(ODocument doc : docs) {
                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);

                Date createTime = doc.field("createTime", Date.class);
                topic.setCreateTime(createTime);

                ODocument accountDoc = doc.field("account");
                Account account = AccountMapper.buildAccount(accountDoc);
                topic.setAccount(account);

                topics.add(topic);
            }
        }
        finally {
           //db.close();
           db.shutdown();
        }
        return topics;
    }

}

package com.tissue.plan.dao.orient;

import com.tissue.core.Account;
import com.tissue.core.User;
import com.tissue.core.dao.orient.ContentDaoImpl;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.core.mapper.UserMapper;
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
import com.tinkerpop.blueprints.Vertex;

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

        OrientGraph db = dataSource.getDB();
        try {
            ODocument doc = TopicMapper.convertTopic(command);
            doc.save();

            id = doc.getIdentity().toString();
            String accountId = command.getAccount().getId();

            String sql = "create edge EdgeCreateTopic from " + accountId + " to " + id + " set category = 'topic', createTime = sysdate()";
            logger.debug(sql);

            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.shutdown();
        }
        return id;
    }

    /**
     * Get a topic with all fields available.
     */
    public Topic getTopic(String topicId) {

        String sql = "select @this as topic, in_EdgeCreateTopic.createTime as createTime, in_EdgeCreateTopic.out as account, in_EdgeCreateTopic.out.out_AccountUser as user from " + topicId;
        logger.debug(sql);

        Topic topic = null;

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:4")).execute();
            for(ODocument doc : docs) {
                ODocument topicDoc = doc.field("topic");
                topic = TopicMapper.buildTopic(topicDoc);

                Date createTime = doc.field("createTime", Date.class);
                topic.setCreateTime(createTime);

                ODocument accountDoc = doc.field("account");
                Account account = AccountMapper.buildAccount(accountDoc);

                ODocument userDoc = doc.field("user");
                User user = UserMapper.buildUser(userDoc);
                
                account.setUser(user);
                topic.setAccount(account);

                List<ODocument> planDocs = topicDoc.field("plans");
                if(planDocs != null) {
                    for(ODocument planDoc : planDocs) {
                        Plan plan = PlanMapper.buildPlan(planDoc);

                        ODocument planAccountDoc = planDoc.field("in_EdgeCreatePlan.out");
                        Account planAccount = AccountMapper.buildAccount(planAccountDoc);
                        plan.setAccount(planAccount);

                        ODocument planUserDoc = planAccountDoc.field("out_AccountUser.");
                        User planUser = UserMapper.buildUser(planUserDoc);
                        planAccount.setUser(planUser);

                        topic.addPlan(plan);
                    }
                }
            }
        }
        finally {
           db.shutdown();
        }
        return topic;
    }

    public Topic getTopicByPlan(String planId) {
        String sql = "select topic from " + planId;
        logger.debug(sql);

        Topic topic = null;

        OrientGraph db = dataSource.getDB();
        try {
            List<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                ODocument topicDoc = doc.field("topic");
                topic = TopicMapper.buildTopic(topicDoc);
                TopicMapper.setupPlans(topic, topicDoc);
            }
        }
        finally {
            db.shutdown();
        }
        return topic;
    }

    public Topic getTopicByPost(String postId) {
        String sql = "select plan.topic as topic from " + postId;
        logger.debug(sql);

        Topic topic = null;

        OrientGraph db = dataSource.getDB();
        try {
            List<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                ODocument topicDoc = doc.field("topic");
                topic = TopicMapper.buildTopic(topicDoc);

                TopicMapper.setupPlans(topic, topicDoc);
             }
        }
        finally {
            db.shutdown();
        }
        return topic;
    }

    public void update(TopicCommand command) {
        OrientGraph db = dataSource.getDB();
        try {
            OrientVertex v = db.getVertex(command.getId());
            v.setProperty("title", command.getTitle());
            v.setProperty("content", command.getContent());
            v.setProperty("tags", command.getTags());
        }
        finally {
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

        OrientGraph db = dataSource.getDB();
        try {
            List<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
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
            db.shutdown();
        }
        return topics;
    }

    /**
     * Get featured topics.
     */
    public List<Topic> getFeaturedTopics(int num) {

        String sql = "select in as topic, out as account, out.out_AccountUser as user, createTime from EdgeCreateTopic where in.deleted is null and in.type = 'featured' order by createTime desc limit " + num;
        logger.debug(sql);

        List<Topic> topics = new ArrayList();

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            for(ODocument doc : docs) {
                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);

                Date createTime = doc.field("createTime", Date.class);
                topic.setCreateTime(createTime);

                ODocument accountDoc = doc.field("account");
                Account account = AccountMapper.buildAccount(accountDoc);
                topic.setAccount(account);

                ODocument userDoc = doc.field("user");
                User user = UserMapper.buildUser(userDoc);
                account.setUser(user);

                topics.add(topic);
            }
        }
        finally {
            db.shutdown();
        }
        return topics;
    }

    /**
     * Get topics count.
     */
    public long getTopicsCount() {
        long count = 0L;

        OrientGraph db = dataSource.getDB();
        try {
            count = db.countVertices("topic");
        }
        finally {
            db.shutdown();
        }
        return count;
    }

    /**
     * Get paged topics reverse ordered by createTime.
     */
    public List<Topic> getPagedTopics(int page, int size) {

        String sql = "select in as topic, out as account, out.out_AccountUser as user, createTime from EdgeCreateTopic where in.deleted is null order by createTime desc skip " + ((page -1) * size) + " limit " + size;
        logger.debug(sql);

        List<Topic> topics = new ArrayList();

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            for(ODocument doc : docs) {
                System.out.println("in topic dao: " + doc);

                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);

                Date createTime = doc.field("createTime", Date.class);
                topic.setCreateTime(createTime);

                ODocument accountDoc = doc.field("account");
                Account account = AccountMapper.buildAccount(accountDoc);

                ODocument userDoc = doc.field("user");
                User user = UserMapper.buildUser(userDoc);
                account.setUser(user);

                topic.setAccount(account);

                topics.add(topic);
            }
        }
        finally {
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

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();

            for(ODocument doc : docs) {
                Set<String> result = doc.field("tags", Set.class); 
                tags = new TreeSet<String>(result);
                break;
            }
            return tags;
        }
        finally {
            db.shutdown();
        }
    }

    public long getTopicsCountByTag(String tag) {

        String sql = "select count(*) from Topic where deleted is null and tags in '" + tag + "'";
        logger.debug(sql);

        long result = 0L;

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            for(ODocument doc : docs) {
                result = doc.field("count", long.class);
            }
            return result;
        }
        finally {
            db.shutdown();
        }
    }

    public List<Topic> getPagedTopicsByTag(String tag, int page, int size) {

        String sql = "select in as topic, out as account, out.out_AccountUser as user, createTime from EdgeCreateTopic where in.deleted is null and in.tags in '" + tag + "' order by createTime desc skip " + (page - 1) * size + " limit " + size;
        logger.debug(sql);

        List<Topic> topics = new ArrayList();

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            for(ODocument doc : docs) {
                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);

                Date createTime = doc.field("createTime", Date.class);
                topic.setCreateTime(createTime);

                ODocument accountDoc = doc.field("account");
                Account account = AccountMapper.buildAccount(accountDoc);

                ODocument userDoc = doc.field("user");
                User user = UserMapper.buildUser(userDoc);
                account.setUser(user);

                topic.setAccount(account);

                topics.add(topic);
            }
        }
        finally {
           db.shutdown();
        }
        return topics;
    }

}

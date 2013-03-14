package com.tissue.core.plan.dao.orient;

import com.tissue.core.command.PlanCommand;
import com.tissue.core.util.OrientDataSource;
import com.tissue.core.mapper.UserMapper;
import com.tissue.core.mapper.TopicMapper;
import com.tissue.core.mapper.PlanMapper;
import com.tissue.core.mapper.PostMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Post;
import com.tissue.core.plan.dao.PlanDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PlanDaoImpl implements PlanDao {
    private static Logger logger = LoggerFactory.getLogger(PlanDaoImpl.class);
    
    @Autowired
    protected OrientDataSource dataSource;

    public String create(PlanCommand plan) {

        String userId = plan.getAccount().getId();
        String topicId = plan.getTopic().getId();

        String id = null;
        
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = PlanMapper.convertPlan(plan);
            db.save(doc);

            id = doc.getIdentity().toString();
            String sql = "create edge EdgeTopic from " + userId + " to " + id + " set label = 'hostGroup', createTime = sysdate()";
            logger.debug(sql);

            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "update " + id + " set topic = " + topicId;
            logger.debug(sql);

            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "update " + topicId + " add plans = " + id;
            logger.debug(sql);

            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.close();
        }
        return id;
    }

    public Plan getPlan(String planId) {
        Plan plan = null;
        String sql = "select from " + planId;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                plan = PlanMapper.buildPlan(doc);
            }
        }
        finally {
            db.close();
        }
        return plan;
    }

    public void addMember(String planId, String accountId) {
        OGraphDatabase db = dataSource.getDB();
        try {
            String sql = "create edge EdgeTopic from " + accountId + " to " + planId + " set label='joinGroup', createTime=sysdate()";
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "update " + planId + " increment count = 1";
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.close();
        }
    }

    public Boolean isMember(String planId, String accountId) {
        //String sql = "select from " + planId + " where in[label='joinGroup'].out in " + accountId;
        String sql = "select from " + planId + " where in.out in " + accountId;
        logger.debug(sql);

        Boolean isMember = false;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql));
            if(!docs.isEmpty()) {
                isMember = true;
            }
        }
        finally {
            db.close();
        }
        return isMember;
    }

    /**
     * Get a topic by plan id with all fields available.
     */
    public Topic getTopic(String planId) {
        Topic topic = null;
        String sql = "select topic from " + planId;
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
     * post
     */
    public long getPostsCount(String planId) {
        long count = 0;

        String sql = "select count(*) from Post where deleted is null and plan in " + planId;

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

    public List<Post> getPagedPosts(String planId, int page, int size) {
        List<Post> posts = new ArrayList();
        String sql = "select from Post where deleted is null and plan in " + planId + " order by createTime desc skip " + (page - 1) * size + " limit " + size;

        OGraphDatabase db = dataSource.getDB();
        try {
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
 
}

package com.tissue.plan.dao.orient;

import com.tissue.core.datasources.OrientDataSource;
import com.tissue.plan.command.PlanCommand;
import com.tissue.plan.mapper.TopicMapper;
import com.tissue.plan.mapper.PlanMapper;
import com.tissue.plan.Topic;
import com.tissue.plan.Plan;
import com.tissue.plan.dao.PlanDao;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
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
            String sql = "create edge EdgeCreatePlan from " + userId + " to " + id + " set createTime = sysdate(), category = 'plan'";
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
        String sql = "select @this as plan, in_[category='plan'].createTime as createTime from " + planId;
        logger.debug(sql);

        OGraphDatabase db = dataSource.getDB();
        try {

            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));

            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0).field("plan");

                plan = PlanMapper.buildPlan(doc);
                Date createTime = doc.field("createTime", Date.class);
                plan.setCreateTime(createTime);

                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                TopicMapper.postProcessTopic(topic, topicDoc);
                plan.setTopic(topic);
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
            String sql = "create edge EdgeCreateMember from " + accountId + " to " + planId + " set createTime = sydate(), category ='member'";
            logger.debug(sql);

            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "update " + planId + " increment count = 1";
            logger.debug(sql);

            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.close();
        }
    }

    public Boolean isMember(String planId, String accountId) {
        String sql = "select from " + planId + " where in_.out in " + accountId;
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

    public List<Plan> getPlansByUser(String userId) {
        String sql = "select from plan where in_.out.user in " + userId;
        logger.debug(sql);

        List<Plan> plans = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                Plan plan = PlanMapper.buildPlan(doc);
                plans.add(plan);

                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                plan.setTopic(topic);
             }
        }
        finally {
            db.close();
        }
        return plans;
    }

    public List<Plan> getPlansByAccount(String accountId) {
        String sql = "select from plan where in_.out in " + accountId;
        logger.debug(sql);

        List<Plan> plans = new ArrayList();
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));

            for(ODocument doc : docs) {

                Plan plan = PlanMapper.buildPlan(doc);
                plans.add(plan);

                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                plan.setTopic(topic);
            }
        }
        finally {
            db.close();
        }
        return plans;
    }

}

package com.tissue.plan.dao.orient;

import com.tissue.core.Account;
import com.tissue.core.datasources.OrientDataSource;
import com.tissue.core.mapper.AccountMapper;
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

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

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
        
        OrientGraph db = dataSource.getDB();
        try {
            ODocument doc = PlanMapper.convertPlan(plan);
            doc.save();

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
            db.shutdown();
        }
        return id;
    }

    public Plan getPlan(String planId) {

        String sql = "select from " + planId;
        logger.debug(sql);

        Plan plan = null;

        OrientGraph db = dataSource.getDB();
        try {
            List<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            if(!docs.isEmpty()) {
                ODocument planDoc = docs.get(0);
                plan = PlanMapper.buildPlan(planDoc);

                PlanMapper.setupCreatorAndTimestamp(plan, planDoc);

                ODocument topicDoc = planDoc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);

                AccountMapper.setupCreatorAndTimestamp(topic, topicDoc);
                TopicMapper.setupPlans(topic, topicDoc);

                plan.setTopic(topic);
            }
        }
        finally {
            db.shutdown();
        }
        return plan;
    }

    public void addMember(String planId, String accountId) {
        OrientGraph db = dataSource.getDB();
        try {
            String sql = "create edge EdgeCreateMember from " + accountId + " to " + planId + " set createTime = sysdate(), category ='member'";
            logger.debug(sql);

            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.shutdown();
        }
    }

    public Boolean isMember(String planId, String accountId) {
        String sql = "select from " + planId + " where in_.out in " + accountId;
        logger.debug(sql);

        Boolean isMember = false;

        OrientGraph db = dataSource.getDB();
        try {
            List<ODocument> docs = db.command(new OSQLSynchQuery(sql)).execute();
            if(!docs.isEmpty()) {
                isMember = true;
            }
        }
        finally {
            db.shutdown();
        }
        return isMember;
    }

    public List<Plan> getPlansByUser(String userId) {
        String sql = "select from plan where in_.out.user in " + userId;
        logger.debug(sql);

        List<Plan> plans = new ArrayList();

        OrientGraph db = dataSource.getDB();
        try {
            List<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            for(ODocument doc : docs) {
                Plan plan = PlanMapper.buildPlan(doc);
                plans.add(plan);

                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                plan.setTopic(topic);
             }
        }
        finally {
            db.shutdown();
        }
        return plans;
    }

    public List<Plan> getPlansByAccount(String accountId) {
        String sql = "select @this as plan, in_EdgeCreatePlan.createTime as createTime, in_EdgeCreatePlan.out as account from plan where in_EdgeCreatePlan.out in " + accountId;
        logger.debug(sql);

        List<Plan> plans = new ArrayList();
        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            for(ODocument doc : docs) {
                ODocument planDoc = doc.field("plan");
                Plan plan = PlanMapper.buildPlan(planDoc);

                Date createTime = doc.field("createTime", Date.class);
                plan.setCreateTime(createTime);

                ODocument accountDoc = doc.field("account");
                Account account = AccountMapper.buildAccount(accountDoc);
                plan.setAccount(account);

                ODocument topicDoc = planDoc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                plan.setTopic(topic);

                plans.add(plan);
             }


            /**
            List<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            for(ODocument doc : docs) {
                Plan plan = PlanMapper.buildPlan(doc);
                PlanMapper.setupCreatorAndTimestamp(plan, doc);
                plans.add(plan);

                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
                plan.setTopic(topic);
            }
            */
        }
        finally {
            db.shutdown();
        }
        return plans;
    }

}

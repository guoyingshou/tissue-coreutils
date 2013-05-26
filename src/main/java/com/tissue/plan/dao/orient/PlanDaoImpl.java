package com.tissue.plan.dao.orient;

import com.tissue.core.Account;
import com.tissue.core.User;
import com.tissue.core.datasources.OrientDataSource;
import com.tissue.core.mapper.AccountMapper;
import com.tissue.core.mapper.UserMapper;
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
import com.tinkerpop.blueprints.Vertex;

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

        String accountId = plan.getAccount().getId();
        String topicId = plan.getTopic().getId();

        OrientGraph db = dataSource.getDB();
        try {
            ODocument doc = PlanMapper.convertPlan(plan);
            doc.save();
            String planId = doc.getIdentity().toString();

            String sql = "create edge Owner from " + planId + " to " + accountId + " set createTime = sysdate(), category = 'plan'";
            logger.debug(sql);
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "create edge PlansTopic from " + planId + " to " + topicId;
            logger.debug(sql);
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            return planId;
        }
        finally {
            db.shutdown();
        }
    }

    public Plan getPlan(String planId) {
        String sql = "select @this as plan, " +
                     "out_PlansTopic as topic " +
                     "from " + planId;
         logger.debug(sql);

        Plan plan = null;
        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:1")).execute();
            for(ODocument doc : docs) {
                ODocument planDoc = doc.field("plan");
                plan = PlanMapper.buildPlan(planDoc);

                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildTopic(topicDoc);
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
            String sql = "create edge Member from " + planId + " to " + accountId + " set createTime = sysdate(), category ='member'";
            logger.debug(sql);

            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
        }
        finally {
            db.shutdown();
        }
    }

    public Boolean isMember(String planId, String accountId) {
        String sql = "select from " + planId + " where " + accountId + " in set(out_Owner.in, out_Member.in)";
        logger.debug(sql);

        Boolean isMember = false;

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<Vertex> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:1")).execute();
            for(Vertex v : docs) {
                isMember = true;
                break;
            }
        }
        finally {
            db.shutdown();
        }
        return isMember;
    }

    public List<Plan> getPlansByUser(String userId) {
        String sql = "select @this as plan, out_PlansTopic as topic " + 
                     "from Plan " +
                     "where set(out_Owner.in.out_AccountsUser, out_Member.in.out_AccountsUser) in " + userId;
        logger.debug(sql);

        List<Plan> plans = new ArrayList();

        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:2")).execute();
            for(ODocument doc : docs) {
                ODocument planDoc = doc.field("plan");
                Plan plan = PlanMapper.buildPlan(planDoc);
                plans.add(plan);

                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildPlainTopic(topicDoc);
                plan.setTopic(topic);
             }
        }
        finally {
            db.shutdown();
        }
        return plans;
    }

    public List<Plan> getPlansByAccount(String accountId) {
        String sql = "select @this as plan, " +
                     "out_PlansTopic as topic " + 
                     "from plan " +
                     "where out_Owner.in in " + accountId;
        logger.debug(sql);

        List<Plan> plans = new ArrayList();
        OrientGraph db = dataSource.getDB();
        try {
            Iterable<ODocument> docs = db.command(new OSQLSynchQuery(sql).setFetchPlan("*:3")).execute();
            for(ODocument doc : docs) {
                ODocument planDoc = doc.field("plan");
                Plan plan = PlanMapper.buildPlan(planDoc);

                ODocument topicDoc = doc.field("topic");
                Topic topic = TopicMapper.buildPlainTopic(topicDoc);
                plan.setTopic(topic);

                plans.add(plan);
             }
        }
        finally {
            db.shutdown();
        }
        return plans;
    }

}

package com.tissue.core.plan.dao.orient;

import com.tissue.core.command.PlanCommand;
import com.tissue.core.util.OrientDataSource;
import com.tissue.core.mapper.UserMapper;
import com.tissue.core.mapper.PlanMapper;
import com.tissue.core.mapper.TopicMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.dao.PlanDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.List;
import java.util.ArrayList;

@Component
public class PlanDaoImpl implements PlanDao {
    
    @Autowired
    protected OrientDataSource dataSource;

    public String create(PlanCommand plan) {
        String id = null;
        
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = PlanMapper.convertPlan(plan);
            db.save(doc);

            id = doc.getIdentity().toString();
            String userId = plan.getUser().getId();
            String topicId = plan.getTopic().getId();

            String sql = "create edge EdgeJoin from " + userId + " to " + id + " set label = 'plan', createTime = sysdate()";
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "update " + id + " set topic = " + topicId;
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "update " + topicId + " add plans = " + id;
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

        //String rid = OrientIdentityUtil.decode(planId);
        String sql = "select from " + planId;
        
        OGraphDatabase db = dataSource.getDB();
        try {
            //ODocument doc = querySingle(db, sql);
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                plan = PlanMapper.buildPlanDetails(doc);
            }
        }
        finally {
            db.close();
        }
        return plan;
    }

    public List<Plan> getPlansByUserId(String userId) {
        List<Plan> plans = new ArrayList();

        //String rid = OrientIdentityUtil.decode(userId);
        String sql = "select from plan where in.out in " + userId;

        OGraphDatabase db = dataSource.getDB();
        try {
            //List<ODocument> docs = query(db, sql);
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            for(ODocument doc : docs) {
                Plan plan = PlanMapper.buildPlan(doc);
                plans.add(plan);
            }
        }
        finally {
            db.close();
        }
        return plans;
    }

    public void addMember(String planId, String userId) {

        //String ridUser = OrientIdentityUtil.decode(userId);
        //String ridPlan = OrientIdentityUtil.decode(planId);

        OGraphDatabase db = dataSource.getDB();
        try {
            String sql = "create edge from " + userId + " to " + planId + " set label='members', createTime=sysdate()";
            //executeCommand(db, sql);
            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
            sql = "update " + planId + " increment count = 1";
            //executeCommand(db, sql);
            cmd = new OCommandSQL(sql);
            db.command(cmd).execute();
 
        }
        finally {
            db.close();
        }
    }

    /**
     * Get a topic by plan id with all fields available.
     */
    public Topic getTopic(String planId) {
        Topic topic = null;

        //String ridPlan = OrientIdentityUtil.decode(planId);
        String sql = "select topic from " + planId;
        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = db.query(new OSQLSynchQuery(sql).setFetchPlan("*:3"));
            if(!docs.isEmpty()) {
                ODocument doc = docs.get(0);
                ODocument topicDoc = doc.field("topic");
                topic = TopicMapper.buildTopicDetails(topicDoc);
            }
        }
        finally {
            db.close();
        }
        return topic;
    }

}

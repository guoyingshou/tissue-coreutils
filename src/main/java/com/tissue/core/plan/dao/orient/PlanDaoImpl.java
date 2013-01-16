package com.tissue.core.plan.dao.orient;

import com.tissue.core.orient.dao.OrientDao;
import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.mapper.PlanMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.dao.PlanDao;

import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;

@Component
public class PlanDaoImpl extends OrientDao implements PlanDao {
    
    public Plan create(Plan plan) {
        
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = PlanMapper.convertPlan(plan);
            saveDoc(doc);

            String ridPlan = doc.getIdentity().toString();
            String ridUser = OrientIdentityUtil.decode(plan.getUser().getId());
            String ridTopic = OrientIdentityUtil.decode(plan.getTopic().getId());

            String sql = "create edge EdgeHost from " + ridUser + " to " + ridPlan + " set label = 'host', createTime = sysdate()";
            executeCommand(db, sql);

            String sql2 = "update " + ridPlan + " set topic = " + ridTopic;
            executeCommand(db, sql2);

            String sql3 = "update " + ridTopic + " add plans = " + ridPlan;
            executeCommand(db, sql3);


            plan.setId(OrientIdentityUtil.encode(ridPlan));
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return plan;
    }

    public Plan getPlan(String planId) {
        String rid = OrientIdentityUtil.decode(planId);
        String sql = "select from " + rid;
        
        OGraphDatabase db = dataSource.getDB();
        try {
        ODocument doc = querySingle(db, sql);
        return PlanMapper.buildPlan(doc);
        }
        finally {
            db.close();
        }
    }

    public List<Plan> getPlans(String topicId) {

        OGraphDatabase db = dataSource.getDB();
        try {
        String rid = OrientIdentityUtil.decode(topicId);
        String sql = "select from plan where topic = " + rid;
        List<ODocument> docs = query(db, sql);

        return PlanMapper.buildPlans(docs);
        }
        finally {
            db.close();
        }
    }

    public void addMember(String planId, String userId) {

        String ridUser = OrientIdentityUtil.decode(userId);
        String ridPlan = OrientIdentityUtil.decode(planId);

        String sql = "create edge EdgeJoin from " + ridUser + " to " + ridPlan + " set label='member', createTime=sysdate()";
        String sql2 = "update " + ridPlan + " increment count = 1";

        OGraphDatabase db = dataSource.getDB();
        try {
            executeCommand(db, sql);
            executeCommand(db, sql2);
        }
        finally {
            db.close();
        }
    }
}

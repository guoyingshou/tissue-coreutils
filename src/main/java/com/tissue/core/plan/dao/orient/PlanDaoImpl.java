package com.tissue.core.plan.dao.orient;

import com.tissue.core.orient.dao.OrientDao;
import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.mapper.UserMapper;
import com.tissue.core.mapper.PlanMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Plan;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.dao.PlanDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import java.util.List;
import java.util.ArrayList;

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

            String sql = "create edge from " + ridUser + " to " + ridPlan + " set label = 'plan', createTime = sysdate()";
            executeCommand(db, sql);

            sql = "update " + ridPlan + " set topic = " + ridTopic;
            executeCommand(db, sql);

            sql = "update " + ridTopic + " add plans = " + ridPlan;
            executeCommand(db, sql);

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
        Plan plan = null;

        String rid = OrientIdentityUtil.decode(planId);
        String sql = "select from " + rid;
        
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = querySingle(db, sql);
            plan = PlanMapper.buildPlanDetails(doc);
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return plan;
    }

    public List<Plan> getPlansByUserId(String userId) {
        List<Plan> plans = new ArrayList();

        String rid = OrientIdentityUtil.decode(userId);
        String sql = "select from plan where in.out in " + rid;

        OGraphDatabase db = dataSource.getDB();
        try {
            List<ODocument> docs = query(db, sql);
            for(ODocument doc : docs) {
                Plan plan = PlanMapper.buildPlan(doc);
                plans.add(plan);
            }
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return plans;
    }

    public void addMember(String planId, String userId) {

        String ridUser = OrientIdentityUtil.decode(userId);
        String ridPlan = OrientIdentityUtil.decode(planId);

        OGraphDatabase db = dataSource.getDB();
        try {
            String sql = "create edge from " + ridUser + " to " + ridPlan + " set label='members', createTime=sysdate()";
            executeCommand(db, sql);

            sql = "update " + ridPlan + " increment count = 1";
            executeCommand(db, sql);
        }
        finally {
            db.close();
        }
    }

}

package com.tissue.core.plan.dao;

import com.tissue.core.command.PlanCommand;
import com.tissue.core.plan.Topic;
import com.tissue.core.plan.Plan;
import java.util.List;

public interface PlanDao {

    //String create(Plan plan);
    String create(PlanCommand planCommand);

    Plan getPlan(String planId);

    List<Plan> getPlansByUserId(String userId);

    void addMember(String planId, String userId);

    Topic getTopic(String planId);

}

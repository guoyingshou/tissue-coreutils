package com.tissue.core.plan.dao;

import com.tissue.core.plan.Plan;
import java.util.List;

public interface PlanDao {

    Plan create(Plan plan);

    Plan getPlan(String planId);

    List<Plan> getPlansByUserId(String userId);

    void addMember(String planId, String userId);

}

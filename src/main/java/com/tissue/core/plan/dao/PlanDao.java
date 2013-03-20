package com.tissue.core.plan.dao;

import com.tissue.core.plan.command.PlanCommand;
import com.tissue.core.plan.Plan;
import java.util.List;

public interface PlanDao {

    String create(PlanCommand planCommand);

    Plan getPlan(String planId);

    void addMember(String planId, String accountId);

    Boolean isMember(String planId, String accountId);

    List<Plan> getPlansByUser(String userId);

    List<Plan> getPlansByAccount(String accountId);

}

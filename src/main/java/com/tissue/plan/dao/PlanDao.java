package com.tissue.plan.dao;

import com.tissue.plan.command.PlanCommand;
import com.tissue.plan.Plan;
import java.util.List;

public interface PlanDao {

    String create(PlanCommand planCommand);

    /**
     * For use by spring converter.
     */
    Plan getPlan(String planId);

    void addMember(String planId, String accountId);

    Boolean isMember(String planId, String accountId);

    List<Plan> getPlansByUser(String userId);

    List<Plan> getPlansByAccount(String accountId);

}

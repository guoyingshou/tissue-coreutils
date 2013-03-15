package com.tissue.core.plan;

import com.tissue.core.UserGeneratedContent2;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

public class Topic extends UserGeneratedContent2 {

    private Set<String> tags;
    private List<Plan> plans;
 
    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void addPlan(Plan plan) {
        if(plans == null) {
            plans = new ArrayList();
        }
        plans.add(plan);
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }

    public List<Plan> getPlans() {
        return plans;
    }

    public Plan getActivePlan() {
        if(plans != null) {
            for(Plan plan : plans) {
                if(plan.isActive())
                    return plan;
            }
        }
        return null;
    }

    public List<Plan> getArchivedPlans() {
        List<Plan> result = new ArrayList();

        if(plans != null) {
            for(Plan plan : plans) {
                if(!plan.isActive()) {
                    result.add(plan);
                }
            }
        }
        return result;
    }
}

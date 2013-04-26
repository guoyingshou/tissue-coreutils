package com.tissue.plan;

import com.tissue.core.UserGeneratedContent2;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import org.joda.time.DateTime;

public class Topic extends UserGeneratedContent2 {

    private Set<String> tags;
    private List<Plan> plans;
 
    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Set<String> getTags() {
        return tags;
    }

    public String getTagsAsString() {
        StringBuilder buf = new StringBuilder();
        for(String tag : tags) {
            buf.append(tag);
            buf.append(" ");
        }
        return buf.toString();
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

    /**
    //-------- Activity implementation

    public String getLabel() {
        return "topic";
    }

    public List<String> getMessageArgs() {
        List<String> args = new ArrayList<String>();

        args.add(id.replece("#", ""));
        if(title.length() > 24) {
            args.add(title.substring(0,24) + "...");
        }
        else {
            args.add(title);
        }
        return args;
    }

    public TimeFormat getTimeBefore() {
        return new TimeFormat(new DateTime(createTime), new DateTime());
    }
    */
}

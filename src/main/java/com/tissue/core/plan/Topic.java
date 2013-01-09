package com.tissue.core.plan;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

public class Topic extends ContentParent {

    private Set<String> tags;
    private List<Plan> plans;
    private List<Post> posts;
    
    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Set<String> getTags() {
        return tags;
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

    public List<Plan> getDeadPlans() {
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

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public long getPostsCount() {
        return posts.size();
    }
}

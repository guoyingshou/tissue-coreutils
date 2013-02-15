package com.tissue.core.plan.command;

import com.tissue.core.social.User;
import com.tissue.core.plan.Plan;
import java.util.Set;

public interface PostCommand {
    String getId();
    String getTitle();
    String getContent();
    String getType();
    Plan getPlan();
    User getUser();
}

package com.tissue.core.command;

import com.tissue.core.plan.Topic;
import com.tissue.core.social.User;

public interface PlanCommand {
    int getDuration();
    User getUser();
    Topic getTopic();
}

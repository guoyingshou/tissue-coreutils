package com.tissue.core.command;

import com.tissue.core.plan.Topic;
import com.tissue.core.social.Account;

public interface PlanCommand {
    int getDuration();
    Topic getTopic();
    Account getAccount();
}

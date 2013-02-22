package com.tissue.core.command;

import com.tissue.core.plan.Topic;
import com.tissue.core.social.Account;

public interface PlanCommand {
    int getDuration();
    Account getAccount();
    Topic getTopic();
}

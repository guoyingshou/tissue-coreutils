package com.tissue.core.plan.command;

import com.tissue.core.Account;
import com.tissue.core.plan.Topic;

public interface PlanCommand {
    int getDuration();
    Topic getTopic();
    Account getAccount();
}

package com.tissue.core.command;

import com.tissue.core.Account;
import com.tissue.core.plan.Topic;

public interface PlanCommand {
    int getDuration();
    Topic getTopic();
    Account getAccount();
}

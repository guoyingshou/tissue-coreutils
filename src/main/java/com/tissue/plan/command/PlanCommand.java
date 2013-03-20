package com.tissue.plan.command;

import com.tissue.core.Account;
import com.tissue.plan.Topic;

public interface PlanCommand {
    int getDuration();
    Topic getTopic();
    Account getAccount();
}

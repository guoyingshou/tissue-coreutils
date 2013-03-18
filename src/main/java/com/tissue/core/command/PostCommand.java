package com.tissue.core.command;

import com.tissue.core.Account;
import com.tissue.core.plan.Plan;

public interface PostCommand extends ContentCommand {
    String getTitle();
    String getType();
    Plan getPlan();
}

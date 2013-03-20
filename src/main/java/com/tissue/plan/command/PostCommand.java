package com.tissue.plan.command;

import com.tissue.core.command.ContentCommand;
import com.tissue.core.Account;
import com.tissue.plan.Plan;

public interface PostCommand extends ContentCommand {
    String getTitle();
    String getType();
    Plan getPlan();
}

package com.tissue.core.command;

import com.tissue.core.social.Account;
import com.tissue.core.plan.Plan;

public interface QuestionCommand {
    String getId();
    String getTitle();
    String getContent();
    String getType();
    Plan getPlan();
    Account getAccount();
}

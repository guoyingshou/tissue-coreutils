package com.tissue.core.command;

import com.tissue.core.social.Account;
import com.tissue.core.plan.Plan;
import java.util.Set;

public interface PostCommand {
    String getId();
    String getTitle();
    String getContent();
    String getType();
    Plan getPlan();
    Account getAccount();
}

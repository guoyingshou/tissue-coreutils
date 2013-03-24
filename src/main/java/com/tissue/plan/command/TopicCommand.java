package com.tissue.plan.command;

import com.tissue.core.Account;
import java.util.Set;

public interface TopicCommand {
    String getId();
    String getTitle();
    String getContent();
    Set<String> getTags();
    Account getAccount();
}

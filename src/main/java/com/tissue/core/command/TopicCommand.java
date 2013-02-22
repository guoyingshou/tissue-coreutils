package com.tissue.core.command;

import com.tissue.core.social.Account;
import java.util.Set;

public interface TopicCommand {
    String getId();
    String getTitle();
    String getContent();
    Set<String> getTags();
    Account getAccount();
}

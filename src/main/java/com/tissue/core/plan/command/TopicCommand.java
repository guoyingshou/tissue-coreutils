package com.tissue.core.plan.command;

import com.tissue.core.social.User;
import java.util.Set;

public interface TopicCommand {
    String getId();
    String getTitle();
    String getContent();
    Set<String> getTags();
    User getUser();
}

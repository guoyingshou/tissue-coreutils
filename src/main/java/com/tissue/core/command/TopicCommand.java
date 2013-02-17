package com.tissue.core.command;

import com.tissue.core.social.User;
import java.util.Set;

public interface TopicCommand {
    String getId();
    String getTitle();
    String getContent();
    Set<String> getTags();
    User getUser();
}

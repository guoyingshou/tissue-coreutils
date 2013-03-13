package com.tissue.core.command;

import com.tissue.core.social.Account;
import java.util.Set;

public interface TopicCommand extends PostCommand {
    Set<String> getTags();
}

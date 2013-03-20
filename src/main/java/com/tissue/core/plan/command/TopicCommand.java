package com.tissue.core.plan.command;

import java.util.Set;

public interface TopicCommand extends PostCommand {
    Set<String> getTags();
}

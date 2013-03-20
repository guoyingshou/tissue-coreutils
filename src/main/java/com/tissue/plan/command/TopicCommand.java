package com.tissue.plan.command;

import java.util.Set;

public interface TopicCommand extends PostCommand {
    Set<String> getTags();
}

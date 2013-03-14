package com.tissue.core.command;

import com.tissue.core.plan.Plan;

public interface ArticleCommand extends PostCommand {
    Plan getPlan();
}

package com.tissue.core.command;

import com.tissue.core.plan.Plan;

public interface QuestionCommand extends PostCommand {
    Plan getPlan();
}
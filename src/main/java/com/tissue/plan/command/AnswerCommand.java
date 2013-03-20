package com.tissue.plan.command;

import com.tissue.core.command.ContentCommand;
import com.tissue.plan.Question;

public interface AnswerCommand extends ContentCommand {
    Question getQuestion();
}

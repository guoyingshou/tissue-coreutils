package com.tissue.core.plan.command;

import com.tissue.core.command.ContentCommand;
import com.tissue.core.plan.Question;

public interface QuestionCommentCommand extends ContentCommand {
    Question getQuestion();
}

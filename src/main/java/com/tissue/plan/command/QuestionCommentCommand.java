package com.tissue.plan.command;

import com.tissue.core.command.ContentCommand;
import com.tissue.plan.Question;

public interface QuestionCommentCommand extends ContentCommand {
    Question getQuestion();
}

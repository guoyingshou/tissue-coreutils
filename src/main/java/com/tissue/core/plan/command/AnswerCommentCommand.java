package com.tissue.core.plan.command;

import com.tissue.core.command.ContentCommand;
import com.tissue.core.plan.Answer;

public interface AnswerCommentCommand extends ContentCommand {
    Answer getAnswer();
}

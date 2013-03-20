package com.tissue.plan.command;

import com.tissue.core.command.ContentCommand;
import com.tissue.plan.Answer;

public interface AnswerCommentCommand extends ContentCommand {
    Answer getAnswer();
}

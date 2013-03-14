package com.tissue.core.command;

import com.tissue.core.plan.Question;

public interface AnswerCommand extends CommentCommand {
    Question getQuestion();
}

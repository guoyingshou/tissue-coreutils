package com.tissue.core.command;

import com.tissue.core.plan.Answer;

public interface AnswerCommentCommand extends CommentCommand {
    Answer getAnswer();
}

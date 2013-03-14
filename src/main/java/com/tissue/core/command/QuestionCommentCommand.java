package com.tissue.core.command;

import com.tissue.core.plan.Question;

public interface QuestionCommentCommand extends CommentCommand {
    Question getQuestion();
}

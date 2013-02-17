package com.tissue.core.command;

import com.tissue.core.plan.Post;

public interface QuestionCommentCommand extends ItemCommand {
    Post getQuestion();
}

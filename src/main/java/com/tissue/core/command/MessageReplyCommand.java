package com.tissue.core.command;

import com.tissue.core.plan.Message;

public interface MessageReplyCommand extends CommentCommand {
    Message getMessage();
}
package com.tissue.core.plan.command;

import com.tissue.core.command.ContentCommand;
import com.tissue.core.plan.Message;

public interface MessageReplyCommand extends ContentCommand {
    Message getMessage();
}

package com.tissue.plan.command;

import com.tissue.core.command.ContentCommand;
import com.tissue.plan.Message;

public interface MessageReplyCommand extends ContentCommand {
    Message getMessage();
}

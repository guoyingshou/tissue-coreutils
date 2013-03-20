package com.tissue.core.social.command;

import com.tissue.core.command.ContentCommand;
import com.tissue.core.User;

public interface ImpressionCommand extends ContentCommand {
    User getTo();
}
